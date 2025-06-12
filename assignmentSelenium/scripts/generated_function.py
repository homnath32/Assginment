def add_numbers():
    """
    Reads two numbers from stdin and returns their sum.
    """
    try:
        num1, num2 = map(float, input().split())
        return num1 + num2
    except ValueError:
        return "Error: Please enter two valid numbers separated by a space."

if __name__ == "__main__":
    print(add_numbers())