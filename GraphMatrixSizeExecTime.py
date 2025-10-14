import pandas as pd
import matplotlib.pyplot as plt

# Load CSV data
df = pd.read_csv("matrix_size_vs_time.csv")

# Plot
plt.figure(figsize=(10, 6))
plt.plot(df['MatrixSize'], df['SequentialTime'], label='Sequential', marker='o')
plt.plot(df['MatrixSize'], df['ParallelTime'], label='Parallel', marker='o')
plt.xlabel('Matrix Size (N x N)')
plt.ylabel('Execution Time (seconds)')
plt.title('Execution Time vs Matrix Size')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("matrix_size_execution_time.png")
plt.show()
