import matplotlib.pyplot as plt
import pandas as pd

# Load CSV
data = pd.read_csv('speedup_results.csv')

# Plot
plt.figure(figsize=(10, 6))
plt.plot(data['Threads'], data['Speedup'], marker='o', label='Measured Speedup')
plt.plot(data['Threads'], data['Threads'], linestyle='--', color='gray', label='Ideal Linear Speedup')  # Reference line

plt.title('Speedup vs. Number of Threads')
plt.xlabel('Number of Threads')
plt.ylabel('Speedup')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.show()
