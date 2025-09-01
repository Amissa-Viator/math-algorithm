import sympy as sp
import calculation


def set_function():
    calculation.function_input = input("Enter a function f(x): ")
    success = calculation.parse_function()
    function = calculation.get_function()

    if success:
        calculation.gradient_x1 = sp.diff(function, calculation.x1)
        calculation.gradient_x2 = sp.diff(function, calculation.x2)

        calculation.gradient_x1_func = sp.lambdify((calculation.x1, calculation.x2), calculation.gradient_x1, 'numpy')
        calculation.gradient_x2_func = sp.lambdify((calculation.x1, calculation.x2), calculation.gradient_x2, 'numpy')

        calculation.calculate_function = sp.lambdify((calculation.x1, calculation.x2), function, 'numpy')

        print("The function was successfully processed")
    else:
        print("The function wasn't successfully processed due to some errors.")


def set_conditions():
    user_conditions = input("Enter condition with values (values x1, x2): ")

    if user_conditions:
        conditions = user_conditions.split(',')
        conditions = [cond.strip() for cond in conditions]

    parsed_conditions = []
    for cond in conditions:
        try:
            if '<=' in cond:
                lhs, rhs = cond.split('<=', 1)
                lhs_expr = sp.parse_expr(lhs.strip())
                rhs_expr = sp.parse_expr(rhs.strip())
                parsed_conditions.append(lhs_expr <= rhs_expr)
            elif '>=' in cond:
                lhs, rhs = cond.split('>=', 1)
                lhs_expr = sp.parse_expr(lhs.strip())
                rhs_expr = sp.parse_expr(rhs.strip())
                parsed_conditions.append(lhs_expr >= rhs_expr)
            elif '<' in cond:
                lhs, rhs = cond.split('<', 1)
                lhs_expr = sp.parse_expr(lhs.strip())
                rhs_expr = sp.parse_expr(rhs.strip())
                parsed_conditions.append(lhs_expr < rhs_expr)
            elif '>' in cond:
                lhs, rhs = cond.split('>', 1)
                lhs_expr = sp.parse_expr(lhs.strip())
                rhs_expr = sp.parse_expr(rhs.strip())
                parsed_conditions.append(lhs_expr > rhs_expr)
            else:
                raise ValueError(f"Invalid condition format: {cond}")
        except (sp.SympifyError, ValueError) as e:
            print(f"Error parsing condition: {cond}. Error: {e}")
            continue

    if len(calculation.conditions) != 0:
        calculation.conditions.clear()

    calculation.conditions = parsed_conditions

def check_point_in_feasible_region(point):
    x1, x2 = sp.symbols('x1 x2')
    for condition in calculation.conditions:
        if not condition.subs({x1: point[0], x2: point[1]}):
            print(f"The point {point} doesn't satisfy the condition: {condition}")
            return False
    return True


def set_start_point():
    while True:
        point = list(map(float, input("Enter the initial point (two values separated by a space): ").split()))

        if check_point_in_feasible_region(point):
            if len(calculation.allPoints) != 0:
                calculation.allPoints.clear()

            calculation.allPoints.append(point)
            print(f"Initial point {point} was added.")
            break
        else:
            print(f"The point {point} doesn't satisfy all conditions, try again.")


def set_data():
    default_values = [calculation.epsilon, calculation.delta, calculation.multiplier]
    print(f"Default values: eps = {default_values[0]}, delta = {default_values[1]}, lambda = {default_values[2]}")

    user_data = input("Enter three values separated by a space (press Enter to keep unchanged): ")

    if user_data:
        values = user_data.split()
        values = [float(v) for v in values]
        values += default_values[len(values):]
    else:
        values = default_values

    while values[0] < 0:
        print("Can not enter negative value for epsilon")
        values[0] = float(input("Enter epsilon value: "))

    while (values[1] < 0) or (values[1] > 1):
        print("The value of delta must be within (0, 1)")
        values[1] = float(input("Enter delta value: "))

    while (values[2] < 0) or (values[2] > 1):
        print("The value of lambda must be within (0, 1)")
        values[2] = float(input("Enter lambda value: "))

    calculation.epsilon = values[0]
    calculation.delta = values[1]
    calculation.multiplier = values[2]


def switch(value):
    match value:
        case 1:
            set_function()
        case 2:
            set_conditions()
        case 3:
            set_start_point()
        case 4:
            set_data()


while True:
    print(" \nMenu: ")
    print("1. Enter a function")
    print("2. Enter constraints")
    print("3. Enter an initial point")
    print("4. Enter values (eps, delta, lambda)")
    print("5. Start calculation")

    user_input = input("Enter your choice (0 for exit): ")

    if user_input == "0":
        print("\nProgram finished")
        break

    if user_input == "5":
        if calculation.function and calculation.allPoints and calculation.conditions:
            calculation.start()
        else:
            print("Please all required data before starting the calculation.")
        continue

    switch(int(user_input))
