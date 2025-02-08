import sys
import json

def main():
    # Expect the candidate code to be present in a file, for example "Solution.py"
    # and test input is passed via command line argument or environment variable.
    # For this example, we assume the test input is passed as a JSON string argument.
    if len(sys.argv) < 2:
        print("Error: Missing test input", file=sys.stderr)
        sys.exit(1)
    
    test_input = sys.argv[1]
    
    # Dynamically import candidate code from Solution.py
    try:
        import importlib.util
        spec = importlib.util.spec_from_file_location("Solution", "./Solution.py")
        solution = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(solution)
    except Exception as e:
        print(f"Compilation/Import Error: {e}", file=sys.stderr)
        sys.exit(1)
    
    # Run the candidate solution. Assume candidate code defines a function called `solution`
    try:
        # You might need to parse test_input based on your problem's requirements.
        # Here, we assume it's a JSON-encoded list.
        import json
        input_data = json.loads(test_input)
        output = solution.solution(input_data)
        # Print result as JSON for consistency.
        print(json.dumps({"result": output}))
    except Exception as e:
        print(f"Runtime Error: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()
