import os
import numpy as np
import matplotlib.pyplot as plt

# ==============================================================================
# --- CUSTOMIZABLE DATA SECTION ---
# Modify these values to update your graphs.
# ==============================================================================

# 1. SMALL OSCILLATIONS (Figure 1 data)
angles_small = [3, 5, 10]
T_exp_small = [1.800, 1.833, 2.250] 

# 2. BAR CHART DATA (Figure 2 data)
categories = ['3°', '5°', '10°', '20°', '60°']
T_exp_vals = [1.800, 1.833, 2.250, 2.000, 2.000]
T_izm_errs = [0.100, 0.083, 0.125, 0.071, 0.071]

# 3. SERIES CONVERGENCE (Figure 3 data)
convergence_angle_deg = 60 

# ==============================================================================
# --- PLOTTING ENGINE (Do not modify unless necessary) ---
# ==============================================================================

plt.rcParams['font.family'] = 'sans-serif'
plt.rcParams['font.size'] = 10
plt.rcParams['axes.edgecolor'] = '#cccccc'
plt.rcParams['axes.linewidth'] = 0.8
plt.rcParams['grid.color'] = '#eeeeee'
plt.rcParams['grid.linewidth'] = 0.5

os.makedirs('plots_final', exist_ok=True)
w = np.sqrt(9.81 / 1.0) # Frequency w = sqrt(g/L)

# Figure 1: Small Oscillations
t = np.linspace(0, 4, 1000)
fig, axs = plt.subplots(1, 3, figsize=(15, 4.5))
colors = ['#4a76a8', '#2ca02c', '#ff7f0e']

for i, (theta0, T_exp, col) in enumerate(zip(angles_small, T_exp_small, colors)):
    theta_teor = theta0 * np.cos(w * t)
    w_exp = 2 * np.pi / T_exp
    theta_exp = theta0 * np.cos(w_exp * t)
    
    axs[i].plot(t, theta_teor, label='Теория (T=2.006 с)', color=col, linewidth=2)
    axs[i].plot(t, theta_exp, label=f'Эксперимент (T={T_exp:.3f} с)', color=col, linestyle='--', linewidth=2)
    axs[i].set_title(f'$\\theta_0 = {theta0}^\\circ$', fontsize=12, fontweight='bold')
    axs[i].set_xlabel('Время t, с')
    if i == 0: axs[i].set_ylabel('Угол $\\theta(t)$, $^\\circ$')
    axs[i].grid(True, linestyle=':', alpha=0.6)
    axs[i].legend(loc='lower center', fontsize=9)
    axs[i].axhline(0, color='black', linewidth=0.5)

plt.suptitle('Малые колебания: Теория vs Эксперимент', fontsize=14, fontweight='bold', y=1.02)
plt.tight_layout()
plt.savefig('plots_final/fig1.png', dpi=300, bbox_inches='tight')
plt.close()

# Figure 2: Bar Chart
T_lin_vals = [2.006, 2.006, 2.006, 2.006, 2.006]
T_nel_vals = [2.006, 2.007, 2.010, 2.021, 2.153]
x = np.arange(len(categories))
width = 0.25

fig, ax = plt.subplots(figsize=(10, 5.5))
ax.bar(x - width, T_exp_vals, width, yerr=T_izm_errs, label='Эксперимент', color='#5b9bd5', capsize=5)
ax.bar(x, T_lin_vals, width, label='Линейная теория', color='#70ad47')
ax.bar(x + width, T_nel_vals, width, label='Нелинейная теория', color='#ffc000')

ax.set_ylabel('Период T, с'); ax.set_xlabel('Начальный угол $\\theta_0$')
ax.set_title('Сравнение периодов', fontsize=13, fontweight='bold')
ax.set_xticks(x); ax.set_xticklabels(categories)
ax.set_ylim(1.5, 2.5); ax.grid(True, axis='y', linestyle=':', alpha=0.5)
ax.legend(loc='upper right')
plt.tight_layout()
plt.savefig('plots_final/fig2.png', dpi=300, bbox_inches='tight')
plt.close()

# Figure 3: Series Convergence
k_val = np.sin(np.radians(convergence_angle_deg / 2))
T_exact = 4 * (1.685750354812508) / w

def get_K_n(n_max):
    K = 0
    for n in range(n_max):
        if n == 0: term = 1.0
        else:
            num = np.prod([2*j - 1 for j in range(1, n+1)])
            den = np.prod([2*j for j in range(1, n+1)])
            term = (num / den) ** 2 * (k_val ** (2 * n))
        K += term
    return K * (np.pi / 2)

n_terms = np.arange(1, 9)
T_vals = [4 * get_K_n(n) / w for n in n_terms]

fig, ax = plt.subplots(figsize=(9, 5))
ax.plot(n_terms, T_vals, marker='o', color='#41719c', label='T(n слагаемых)')
ax.axhline(T_exact, color='red', linestyle='--', label='Точное значение')
ax.set_title(f'Сходимость ряда K(k) для $\\theta_0 = {convergence_angle_deg}^\\circ$')
ax.set_xlabel('Число слагаемых'); ax.set_ylabel('Период T, с')
ax.grid(True, linestyle=':', alpha=0.5); ax.legend()
plt.tight_layout()
plt.savefig('plots_final/fig3.png', dpi=300, bbox_inches='tight')
plt.close()

print("Graphs successfully generated in 'plots_final' folder.")