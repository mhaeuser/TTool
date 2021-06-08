#!/bin/bash
echo "#define STRING_HELP \" \\n\\" > "$1"
while IFS= read -r line; do
	echo "${line//\"/\\\"} \\n\\" >> "$1"
done < "$2"
echo "\"" >> "$1"