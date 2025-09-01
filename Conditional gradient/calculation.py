from scipy.optimize import linprog
import sympy as sp
import math
import numpy as np
import matplotlib.pyplot as plt

epsilon = 0.0
delta = 0.0
multiplier = 0.0

conditions = []
allPoints = []
function_values = []

function_input = ""
x1, x2 = sp.symbols('x1 x2')
function = None
gradient_x1 = None
gradient_x2 = None
gradient_x1_func = None
gradient_x2_func = None
calculate_function = None


def parse_function():
    global function
    if function_input:
        try:
            function = sp.sympify(function_input)
            return True
        except sp.SympifyError:
            print("Function not valid. Please try again.")
            function = None
            return False
    return False


def get_function():
    return function


def euclidean_metric(next_points, points):
    value = 0.0

    for i in range(len(points)):
        value += pow(next_points[i] - points[i], 2)

    return math.sqrt(value)


def parse_conditions_to_linprog_format():
    global conditions

    symbols = sp.symbols('x1 x2')

    A_ub = []
    b_ub = []
    A_eq = []
    b_eq = []

    for condition in conditions:
        if isinstance(condition, sp.Rel):
            lhs_expr = condition.lhs
            rhs_expr = condition.rhs
            op = str(condition.rel_op)

            lhs_coeffs = [lhs_expr.coeff(sym) for sym in symbols]
            rhs_val = rhs_expr.evalf()

            if op in ['<=', '<']:
                A_ub.append(lhs_coeffs)
                b_ub.append(rhs_val)
            elif op in ['>=', '>']:
                A_ub.append([-coeff for coeff in lhs_coeffs])
                b_ub.append(-rhs_val)
            elif op == '=':
                A_eq.append(lhs_coeffs)
                b_eq.append(rhs_val)

    num_vars = len(symbols)
    for row in A_eq:
        if len(row) < num_vars:
            row.extend([0] * (num_vars - len(row)))

    A_ub = [row if isinstance(row, list) else [row] for row in A_ub]
    A_eq = [row if isinstance(row, list) else [row] for row in A_eq]

    if not A_eq:
        A_eq = [[] for _ in range(num_vars)]
    if not b_eq:
        b_eq = []

    return A_ub, b_ub, A_eq, b_eq


def solve_linear_program(gradient, point):
    c = gradient
    free_member = (-1)*gradient[0]*point[0] - gradient[1]*point[1]

    A_ub, b_ub, A_eq, b_eq = parse_conditions_to_linprog_format()

    bounds = [(0, None), (0, None)]

    if A_eq and b_eq:
        result = linprog(c, A_ub=A_ub, b_ub=b_ub, A_eq=A_eq, b_eq=b_eq, bounds=bounds, method='simplex')
    else:
        result = linprog(c, A_ub=A_ub, b_ub=b_ub, bounds=bounds, method='simplex')


    if result.success:
        optimal_x = result.x
        optimal_fun = result.fun + free_member

        solution = [optimal_x, optimal_fun]
        return solution
    else:
        print("Wasn't found the optimal solution.")
        print(result)
        return None


def find_descent_direction(opt_points, points):
    descent_direction = []
    for i in range(len(opt_points)):
        descent_direction.append(opt_points[i] - points[i])

    return descent_direction


def check_condition_for_alpha(opt_funct, descent, alpha, point):
    global delta, calculate_function

    right_side = delta*alpha*opt_funct
    values = [0] * len(point)

    for i in range(len(point)):
        values[i] = point[i] + alpha*descent[i]

    left_side = calculate_function(values[0], values[1]) - calculate_function(point[0], point[1])

    return left_side <= right_side


def conditional_method():
    global allPoints, function_values, calculate_function
    global epsilon, multiplier, gradient_x1_func, gradient_x2_func

    coefficient = 0
    alpha = 1.0

    while True:
        previous_point = allPoints[coefficient]
        next_point = []
        function_opt = None

        function_values.append(calculate_function(previous_point[0], previous_point[1]))
        gradient = [gradient_x1_func(previous_point[0], previous_point[1]), gradient_x2_func(previous_point[0], previous_point[1])]

        opt_solution = solve_linear_program(gradient, previous_point)
        if opt_solution is not None:
            optimal_point = opt_solution[0]
            function_opt = opt_solution[1]

        if function_opt is not None:
            if function_opt == 0:
                break
            elif function_opt < 0:
                descent_values = find_descent_direction(optimal_point, previous_point)

                while not check_condition_for_alpha(function_opt, descent_values, alpha, previous_point):
                   alpha *= multiplier

                for i in range(len(descent_values)):
                    next_point.append(previous_point[i] + alpha * descent_values[i])

                allPoints.append(next_point)

                if euclidean_metric(next_point, previous_point) < epsilon:
                    function_for_next_point = calculate_function(next_point[0], next_point[1])
                    function_values.append(function_for_next_point)
                    break

        else:
            print("Simplex method calculation error.")
            break

        coefficient += 1

    if len(allPoints) > 1 and all(
            abs(allPoints[-1][i] - allPoints[-2][i]) < epsilon for i in range(len(allPoints[-1]))):
        allPoints.pop()
        function_values.pop()


def print_all_data():
    print("\nResult: ")
    for i in range(len(function_values)):
        points = allPoints[i]
        print(f"Iteration {i}: [{points[0]}, {points[1]}] f(x) = {function_values[i]}")


def show_graph():
    global conditions, allPoints

    x = np.linspace(-5, 5, 400)
    y = np.linspace(-5, 5, 400)
    X, Y = np.meshgrid(x, y)

    Z = (X - 1) ** 2 + (Y - 1) ** 2

    fig, ax = plt.subplots()

    CS = ax.contour(X, Y, Z, levels=np.linspace(0, 8, 20), colors='black')
    ax.clabel(CS, inline=1, fontsize=10)

    feasible_region = np.ones(X.shape, dtype=bool)

    for condition in conditions:
        condition_func = sp.lambdify((x1, x2), condition)
        feasible_region &= condition_func(X, Y)


    for condition in conditions:
        condition_func = sp.lambdify((x1, x2), condition)
        Z_condition = condition_func(X, Y)
        ax.contour(X, Y, Z_condition, levels=[0], colors='black', linestyles='dashed')


    ax.contourf(X, Y, feasible_region, levels=[0, 1], colors=['lightblue'], alpha=0.5)


    if len(allPoints) > 1:
        points_x, points_y = zip(*allPoints)
        ax.plot(points_x, points_y, 'ro-')


    for i, point in enumerate(allPoints):
        ax.text(point[0], point[1], f'  x({i})', color='black', verticalalignment='bottom')


    if allPoints:
        ax.plot(allPoints[0][0], allPoints[0][1], 'go', label='Start Point')
        ax.plot(allPoints[-1][0], allPoints[-1][1], 'bo', label='End Point')

    ax.set_title("Feasible Region and Iteration Path")
    ax.legend()
    plt.grid()
    plt.show()


def start():
    global allPoints, function_values

    print("\n The task's values: ")

    if not allPoints:
        print("The initial point wasn't entered")
        return
    if not function:
        print("The function wasn't defined.")
        return
    if conditions:
        print("Constraints:")
        for cond in conditions:
            print(f"  {cond}")
    else:
        print("The constraints weren't entered.")

    print(f"eps = {epsilon}, delta = {delta}, lambda = {multiplier}")
    print(f"The initial point: {allPoints}")
    print(f"Function: {function}")
    print(f"Gradient: [{gradient_x1}, {gradient_x2}]")

    allPoints = [allPoints[0]]
    function_values = []

    conditional_method()
    print_all_data()
    show_graph()





