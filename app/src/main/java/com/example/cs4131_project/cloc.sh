#!/bin/sh

# Script to count non-comment, non-import/package, non-blank lines in Kotlin files,
# list them, and provide a summary by top-level directory or "Others".
# POSIX sh compatible, avoiding Bash 4+ features and problematic expr calls.

# --- Setup ---
total_lines=0
max_path_length=0
format_width=0 # Initialize formatting width

# Create temporary files securely
tmp_file_list=$(mktemp /tmp/cloc_files.XXXXXX) || { echo "Failed to create temp file list"; exit 1; }
tmp_summary_data=$(mktemp /tmp/cloc_summary.XXXXXX) || { echo "Failed to create temp summary file"; exit 1; }

# Ensure temporary files are cleaned up on exit (normal, Ctrl+C, error)
trap 'rm -f "$tmp_file_list" "$tmp_summary_data"' EXIT HUP INT QUIT TERM

# --- First Pass: Get file list and find max path length ---
# Use NUL delimiters for safety with weird filenames
# Note: Reading NUL delimiters requires specific shell features/tools.
# We'll switch to newline separation, assuming filenames don't contain newlines.
# If filenames contain newlines, this script (and many shell scripts) will break.
git ls-files "*.kt" > "$tmp_file_list"

# Check if any files were found
if [ ! -s "$tmp_file_list" ]; then
    echo "No *.kt files found in git index."
    # Clean up trap requires files to exist, create dummy empty ones if needed for trap robustness?
    # Or just exit here. Trap should handle non-existent files gracefully anyway.
    exit 0
fi

# Read the newline-delimited list to find the max length
while IFS= read -r file; do
  # Calculate file path length reliably using printf and wc -c
  # printf %s doesn't add a newline, wc -c counts bytes (chars for ASCII/simple UTF-8)
  # tr -d ' ' removes any whitespace padding from wc output
  file_length=$(printf "%s" "$file" | wc -c | tr -d ' ')
  # Ensure file_length is a number, default to 0 if calculation failed
  file_length=${file_length:-0}

  if [ "$file_length" -gt "$max_path_length" ]; then
    max_path_length=$file_length
  fi
done < "$tmp_file_list"

# --- Determine formatting width ---
format_width=$max_path_length
header_label_file="File"
header_label_total="Total:"
min_width=15 # Adjust minimum width as needed

# Calculate lengths of labels reliably
len_hlf=$(printf "%s" "$header_label_file" | wc -c | tr -d ' ')
len_hls=$(printf "%s" "$header_label_summary" | wc -c | tr -d ' ')
len_gt=$(printf "%s" "$header_label_grand_total" | wc -c | tr -d ' ')

# Ensure lengths are numbers, default to 0
len_hlf=${len_hlf:-0}
len_hls=${len_hls:-0}
len_gt=${len_gt:-0}

# Ensure minimum width
if [ "$format_width" -lt "$min_width" ]; then
    format_width=$min_width
fi
# Compare with label lengths
if [ "$len_hlf" -gt "$format_width" ]; then
    format_width=$len_hlf
fi
if [ "$len_hls" -gt "$format_width" ]; then
    format_width=$len_hls
fi
if [ "$len_gt" -gt "$format_width" ]; then
    format_width=$len_gt
fi

echo ""

# --- Print File Header ---
printf "%-${format_width}s | %9s\n" "$header_label_file" "Lines"
# Generate separator line
dashes=$(printf "%-${format_width}s" "" | tr ' ' '-')
printf "%s-|----------\n" "$dashes"

# --- Second Pass: Process files, count lines, accumulate totals, update summary file ---
while IFS= read -r file; do
  # Skip if file doesn't exist (e.g., removed after ls-files)
  if [ ! -f "$file" ]; then
      continue
  fi

  # Count lines: remove block comments, then remove line comments, import, package, blank lines
  lines=$(sed -e '/\/\*/,/\*\//d' "$file" | grep -vE '^[[:space:]]*(\/\/|import|package)' | grep -vE '^[[:space:]]*$' | wc -l | tr -d ' ')

  # Handle potential empty output from wc or non-numeric result
  lines=$(echo "$lines" | grep '^[0-9]*$' || echo 0) # Ensure it's numeric or 0

  # Print file and its line count
  printf "%-${format_width}s | %9d\n" "$file" "$lines"

  # Add to grand total using POSIX arithmetic
  total_lines=$((total_lines + lines))

  # Determine the base directory key for summary using POSIX tools
  summary_key=""
  # Check if file path contains a slash using case (more portable than [[)
  case "$file" in
    */*)
      # Extract first path component (POSIX way)
      summary_key=$(echo "$file" | cut -d/ -f1)
      ;;
    *)
      # File is in the root directory
      summary_key="Others"
      ;;
  esac

  # --- Update summary data in the temporary file ---
  # Store format: key<TAB>count (using TAB as delimiter is safer for keys)
  # Escape key for grep fixed string search if needed, but TAB helps
  # Use awk for safer update: find line with key, print updated, else print original, and add if not found
  awk -v key="$summary_key" -v add="$lines" '
    BEGIN { found=0; FS=OFS="\t" }
    $1 == key { $2 += add; found=1; print; next }
    { print }
    END { if (!found) print key, add }
  ' "$tmp_summary_data" > "$tmp_summary_data.new" && mv "$tmp_summary_data.new" "$tmp_summary_data"

done < "$tmp_file_list" # Read from the saved file list

# --- Print Separator Before Summary ---
printf "%s-|----------\n" "$dashes"

# --- Print Summary ---
# Sort summary data (key<TAB>count) by key (field 1). Handle "Others" specifically.

# Print sorted directories first
# Use grep -v to exclude "Others", sort by key (field 1), then print using awk for safety
grep -v '^Others\t' "$tmp_summary_data" | sort -k1,1 | awk -v width="$format_width" '
  BEGIN { FS=OFS="\t" }
  { printf "%-" width "s | %9d\n", $1, $2 }
'

# Print "Others" if it exists
others_line=$(grep '^Others\t' "$tmp_summary_data")
if [ -n "$others_line" ]; then
    # Use awk to extract count safely, handling potential issues if line format is weird
    count=$(echo "$others_line" | awk 'BEGIN{FS=OFS="\t"} {print $2}')
    count=${count:-0} # Default to 0 if awk failed
    key="Others"
    printf "%-${format_width}s | %9d\n" "$key" "$count"
fi

# --- Print Separator Before Grand Total ---
printf "%s-|----------\n" "$dashes"

# --- Print Grand Total ---
printf "%-${format_width}s | %9d\n" "$header_label_total" "$total_lines"

echo "" # End with a blank line

# Trap will clean up temp files on exit
exit 0