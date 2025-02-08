#!/bin/bash
# Expect test input as the first argument (JSON string)

if [ -z "$1" ]; then
  echo "Error: Missing test input" >&2
  exit 1
fi

TEST_INPUT=$1

# Assume the candidate submitted a file named Solution.java that defines a public class Solution with a static method solution(String testInput)
# First, compile the candidate's code.
javac Solution.java 2> compile_error.log
if [ $? -ne 0 ]; then
  echo "Compilation Error:" >&2
  cat compile_error.log >&2
  exit 1
fi

# Run the compiled code and pass the test input as an argument.
# For example, if the solution method takes a JSON string.
OUTPUT=$(java Solution "$TEST_INPUT" 2> runtime_error.log)
if [ $? -ne 0 ]; then
  echo "Runtime Error:" >&2
  cat runtime_error.log >&2
  exit 1
fi

# Output the result (could be further formatted)
echo $OUTPUT