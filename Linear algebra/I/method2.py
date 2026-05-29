import numpy as np

# Вершины тетраэдра
A = np.array([1, 2, 0])
B = np.array([2, 1, 0])
C = np.array([0, 2, 1])
D = np.array([1, 0, 3])
vertices = [A, B, C, D]

# Функция смешанного произведения
def mixed_product(u, v, w):
    return np.dot(np.cross(u, v), w)

# Точки для проверки
points = {
    'P_inside': np.array([1, 5/4, 1]),
    'P_outside': np.array([0, 0, 0]),
    'P_face': np.array([1, 3/2, 1/2]),  # точка точно на грани ABC
    'P_edge': np.array([3/2, 3/2, 0]),
    'P_vertex': A
}

# Определяем векторы граней
AB = B - A
AC = C - A
AD = D - A
BC = C - B
BD = D - B

# Смешанные произведения с противоположными вершинами
opposite = {
    'ABC': mixed_product(AB, AC, AD),
    'ABD': mixed_product(AB, AD, AC),
    'ACD': mixed_product(AC, AD, AB),
    'BCD': mixed_product(BC, BD, A - B)
}

# Допуск для проверки "на грани" (из-за числовых ошибок)
tol = 1e-8

# Проверка каждой точки
for name, P in points.items():
    if any(np.array_equal(P, V) for V in vertices):
        position = "точка совпадает с вершиной"
    else:
        mp_ABC = mixed_product(AB, AC, P - A)
        mp_ABD = mixed_product(AB, AD, P - A)
        mp_ACD = mixed_product(AC, AD, P - A)
        mp_BCD = mixed_product(BC, BD, P - B)

        # Если точка на грани или ребре
        if any(abs(mp) < tol for mp in [mp_ABC, mp_ABD, mp_ACD, mp_BCD]):
            position = "точка лежит на грани или ребре"
        else:
            # Проверка знаков для внутри/вне
            signs_match = all(
                np.sign(mp) == np.sign(opposite[face])
                for mp, face in zip([mp_ABC, mp_ABD, mp_ACD, mp_BCD],
                                    ['ABC','ABD','ACD','BCD'])
            )
            if signs_match:
                position = "точка внутри тетраэдра"
            else:
                position = "точка вне тетраэдра"
    print(f"{name}: {position}")
