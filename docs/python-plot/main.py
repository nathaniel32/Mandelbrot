import matplotlib.pyplot as plt

# Data from each scenario
scenarios = ["1 Worker", "2 Workers", "3 Workers"]
times_ms = [67481, 29229, 12771]

# Chunk distribution per worker
chunk_distribution = [
    {"Intel Pentium Silver": 1200},
    {"Intel Pentium Silver": 547, "ARM Cortex-A76": 653},
    {"Intel Pentium Silver": 229, "ARM Cortex-A76": 274, "Intel i7 Gen 8": 697}
]

# Create a bar chart for processing time
plt.figure(figsize=(12, 6))

plt.subplot(1, 2, 1)
plt.bar(scenarios, times_ms, color=["skyblue", "lightgreen", "salmon"])
plt.title("Processing Time per Scenario")
plt.ylabel("Time (ms)")
plt.xlabel("Worker Configuration")

# Create stacked bar chart for chunk distribution
plt.subplot(1, 2, 2)

labels = scenarios
width = 0.6
worker_labels = ["Intel Pentium Silver", "ARM Cortex-A76", "Intel i7 Gen 8"]
colors = ["#1f77b4", "#ff7f0e", "#2ca02c"]

bottom = [0] * len(labels)
for worker in worker_labels:
    data = [config.get(worker, 0) for config in chunk_distribution]
    plt.bar(labels, data, width, bottom=bottom, label=worker, color=colors[worker_labels.index(worker)])
    bottom = [sum(x) for x in zip(bottom, data)]

plt.title("Chunk Distribution per Worker")
plt.ylabel("Chunks Processed")
plt.xlabel("Worker Configuration")
plt.legend()

plt.tight_layout()
plt.show()
