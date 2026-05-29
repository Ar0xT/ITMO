import numpy as np

# ===== Вершины тетраэдра =====
A = np.array([1.0, 2.0, 0.0])
B = np.array([2.0, 1.0, 0.0])
C = np.array([0.0, 2.0, 1.0])
D = np.array([1.0, 0.0, 3.0])  # D'

EPS = 1e-12


def same_side(normal, ref_point, test_point, base_point):
    """Проверка: по одну ли сторону плоскости точки"""
    s_ref = np.dot(normal, ref_point - base_point)
    s_test = np.dot(normal, test_point - base_point)
    return s_ref * s_test >= -EPS


def point_in_tetrahedron_method1(P):
    """Метод знаков смешанных произведений"""

    # ---- Проверка полупространств ----
    n_ABC = np.cross(B - A, C - A)
    n_ABD = np.cross(B - A, D - A)
    n_ACD = np.cross(C - A, D - A)
    n_BCD = np.cross(C - B, D - B)

    conditions = [
        same_side(n_ABC, D, P, A),
        same_side(n_ABD, C, P, A),
        same_side(n_ACD, B, P, A),
        same_side(n_BCD, A, P, B),
    ]

    if not all(conditions):
        return "снаружи"

    # ---- Классификация (грани / рёбра / вершины) ----
    zero_count = 0
    faces = [
        (n_ABC, A),
        (n_ABD, A),
        (n_ACD, A),
        (n_BCD, B),
    ]

    for normal, base in faces:
        if abs(np.dot(normal, P - base)) <= EPS:
            zero_count += 1

    if zero_count == 0:
        return "внутри"
    elif zero_count == 1:
        return "на грани"
    elif zero_count == 2:
        return "на ребре"
    else:
        return "в вершине"


# ===== Тестовые точки (ВНУТРИ КОДА) =====
test_points = [
    (np.array([1, 1.25, 1.0]), "внутри"),
    (np.array([0.6, 1.8, 100]), "на грани"),
    (np.array([1.5, 100, 0.0]), "на ребре"),
    (np.array([1.0, 2.0, 0.0]), "в вершине"),
    (np.array([0.0, 0.0, 0.0]), "снаружи"),
    (np.array([-1.0, -1.25, -1.0]), "снаружи"),
]

# ===== ВЫВОД (ТОЧНО КАК У ТЕБЯ) =====
print("Метод 1 (метод знаков смешанных произведений):")
print("-" * 60)

for P, expected in test_points:
    result = point_in_tetrahedron_method1(P)
    status = "+" if result == expected else "-"
    print(f"P={P.tolist()}: {result:15} ожидалось: {expected:15} {status}")
