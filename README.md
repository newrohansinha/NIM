# NIM Research Toolkit — Sprague–Grundy, Imperfect Play, Stabilization Analysis

README generated on 2025-10-18. **Math‑first**. GitHub math: `$...$` inline, `$$...$$` display.

---

## 0) Repo Map

```
📄 AccuracyCombinations_1D.java
📄 BoardGenerator_2D.java
📄 GrundyNumberTableGenerator.java
📄 ModFinder.java
📄 MultiOptimalMoveFinder.java
📄 MultiPileOptimal.java
📄 MultiPileWinStatementFinder.java
📄 MultipilePercentWin.java
📄 RawData.pdf
📄 SinglePileStabilzationPointFinder.ipynb
📄 Strategy1.java
📄 Strategy2_1D.java
📄 Strategy3_1D.java
📄 Strategy4_1D.java
📄 TwoPileStabilizationPointFinder.ipynb
📄 TwoPileWinPercentIncrementor.java
📄 compareAccuracyChangingSticks.java
```

---

## 1) Rules implemented in this codebase

- **Single‑pile subtraction game** with move set $M=\{1,x,y\}$. From $n\in\mathbb N_0$, legal successors are $n-1$, $n-x$, $n-y$ (when nonnegative). Files: `GrundyNumberTableGenerator.java`, `Strategy*_1D.java`, `compareAccuracyChangingSticks.java`, `AccuracyCombinations_1D.java`.
- **Two/multi‑pile sum**: piles are independent subtraction games (each pile may have its own $(x_i,y_i)$). The overall position is the disjunctive sum; Grundy numbers XOR. Files: `MultiPileOptimal.java`, `MultiOptimalMoveFinder.java`, `TwoPileWinPercentIncrementor.java`, `MultipilePercentWin.java`.
- **Imperfect play**: each player has accuracy $a\in[0,1]$. With prob. $a$ they take an **optimal** move; otherwise choose a random valid move (bounded by that pile’s $\max(1,x_i,y_i)$). Moves and wins are Monte‑Carlo tallied.

---

## 2) Sprague–Grundy for subtraction $\langle 1,x,y\rangle$

Let $g(n)$ be the Grundy of a single pile with $n$ stones and move set $M=\{1,x,y\}$.

- Base: $g(0)=0$.
- Recurrence:
$$
g(n)=\mathrm{mex}\big\{\, g(n-1),\ g(n-x),\ g(n-y)\,\big\}\quad \text{(ignore negative indices).}
$$

For multiple piles with sizes $n_1,\dots,n_k$ (and per‑pile $x_i,y_i$), overall Grundy
$$
G\;=\;g_1(n_1)\;\oplus\;g_2(n_2)\;\oplus\;\cdots\;\oplus\;g_k(n_k).
$$

**Win criterion (normal play)**: first player wins iff $G\ne 0$.

---

## 3) Single‑pile DP tables (as in `GrundyNumberTableGenerator.java`)

Iterate $n=1..N$ and compute $g(n)$ by mex of reachable $g$. A simpler **win/lose table** `winTable[n]` is also used in several programs:

- `winTable[n]=1` if any legal move reaches a losing state;
- else `winTable[n]=2` (losing for the player to move).

Pseudo:

```text
winTable[0] = 2
for n=1..N:
  winTable[n] = 2
  if n-1 >= 0 and winTable[n-1] == 2: winTable[n] = 1
  if n-x >= 0 and winTable[n-x] == 2: winTable[n] = 1
  if n-y >= 0 and winTable[n-y] == 2: winTable[n] = 1
```

---

## 4) Optimal move logic used by the sims

- **Single pile**: choose the smallest $m\in M$ such that $n-m\ge 0$ and `winTable[n-m]==2`. If none, take any valid move.
- **Multi‑pile**: compute each pile’s Grundy via the $\langle 1,x_i,y_i\rangle$ recurrence; choose any pile $i$ and move $m\in M_i$ yielding $g_i(n_i-m)$ so that the new XOR $G’=0$. If multiple piles or moves satisfy this, tie‑breakers in code may pick **min removal** or random among minima (see `MultipilePercentWin.java`).

Formally, for target $G=\bigoplus_j g_j(n_j)\ne 0$, pick pile $i$ with some option $m\in M_i$ such that
$$
g_i(n_i-m) \,=\, g_i(n_i)\oplus G,
\qquad n_i-m\ge 0.
$$

---

## 5) Imperfect play model (exactly as coded)

Let player accuracy be $a\in[0,1]$. On their turn:

- With prob. $a$: **optimal move** (per §4).
- With prob. $1-a$: **random valid** move on a random non‑empty pile; the random removal is uniformly drawn from $\{1,\dots,\min(n_i,\max(1,x_i,y_i))\}$.

The simulators (`Strategy*.java`, `TwoPileWinPercentIncrementor.java`, `MultipilePercentWin.java`) run $N\in\{10^3,10^4,10^5\}$ games and report empirical win rates.

---

## 6) “Multiple optimal moves” detection (`MultiOptimalMoveFinder.java`)

For a single pile with parameters $(x,y)$, a position $n$ has **two optimal subtractions** when both $n-x$ and $n-y$ are losing states:

```text
optimalX = (n-x >= 0 and winTable[n-x] == 2)
optimalY = (n-y >= 0 and winTable[n-y] == 2)
if optimalX && optimalY → record n
```

The program enumerates $(x,y)$ in bounded grids and prints all such $n$.

---

## 7) Stabilization analysis (Python scripts in repo)

### 7.1 Single‑pile (`SinglePileStabilzationPointFinder.ipynb` — plain Python)

- Smooth win‑rate curve by **moving average** of window $w$:
  $$
  \tilde S[i] = \frac{1}{w}\sum_{j=i-w+1}^{i} S[j].
  $$
- Instantaneous rate of change via discrete gradient with spacing $\Delta x$:
  $$
  S'[i] \approx \frac{\tilde S[i+1]-\tilde S[i-1]}{2\,\Delta x}.
  $$
- **Dynamic window criterion**: earliest index $t$ such that
  $$
  \max_{u\in[t,T]} \lvert S'[u]\rvert\;-\;\min_{u\in[t,T]} \lvert S'[u]\rvert\;<\;\tau
  $$
  (code uses `derivative_threshold = 0.06`). Return $x_t$ and $\tilde S[t]$.

### 7.2 Two‑pile surface (`TwoPileStabilizationPointFinder.ipynb` — plain Python)

- Build grid $Z[i,j]$ of win‑rates over pile lengths $(x_i,y_j)$.
- **Gaussian smoothing** (SciPy `gaussian_filter`) at scale $\sigma$:
  $$
  Z_\sigma = G_\sigma * Z,\qquad
  G_\sigma(x,y)=\frac{1}{2\pi\sigma^2}\exp\!\Big(-\tfrac{x^2+y^2}{2\sigma^2}\Big).
  $$
- Gradient magnitude:
  $$
  \lVert\nabla Z_\sigma\rVert = \sqrt{(\partial_x Z_\sigma)^2 + (\partial_y Z_\sigma)^2},
  $$
  computed via `np.gradient`.
- **Windowed threshold**: inside a logical window (code uses rectangular bands around diagonals), take the argmin of $\lVert\nabla Z_\sigma\rVert$ subject to $\lVert\nabla Z_\sigma\rVert<\tau$.
- Returns $(x^*,y^*,Z_\sigma(x^*,y^*))$ and plots a 3D scatter with the point annotated.

---

## 8) Complexity (matching code paths)

- Single‑pile DP table: $O(N)$.
- Grundy for a pile by memoized mex: $O(N)$ per $(x,y)$.
- Multi‑pile optimal choice: $O(k)$ after per‑pile $g(n)$ is known.
- One simulated episode with $k$ piles and average $T$ moves: $O(Tk)$.

---

## 9) Build & Run

### Java (JDK 17+)

Compile:

```bash
javac *.java
```

Examples:

```bash
# Single‑pile accuracy vs. optimal play
java Strategy1

# Two‑pile heatmap sweep and win‑percent logs
java TwoPileWinPercentIncrementor

# Multi‑pile Monte‑Carlo with per‑pile (x_i,y_i) and accuracy
java MultipilePercentWin

# Enumerate positions with multiple optimal subtractions for (x,y)
java MultiOptimalMoveFinder
```

These programs are **interactive**: they prompt for $x,y$, start sticks, step sizes, and player accuracies.

### Python (analysis)

```bash
# 1D stabilization (moving average, derivative window)
python SinglePileStabilzationPointFinder.ipynb

# 2D stabilization (gaussian_filter + ∇ magnitude on grid)
python TwoPileStabilizationPointFinder.ipynb
```

> Note: these “.ipynb” files contain plain Python code; run them as scripts or paste into a notebook.

---

## 10) Appendix (formulas referenced by code)

- $\mathrm{mex}(S)=\min\{m\in\mathbb N_0\mid m\notin S\}$.
- XOR laws: $a\oplus a=0$, $a\oplus 0=a$, associativity/commutativity.
- Cardinality for persistence: $\Pi(A)=\lvert A\rvert$ (avoid `#` in GitHub math).

---

## 11) Reproducibility

- Random seeds are set via Java’s `Random` or `rand` objects in each class.
- Output logs: e.g., `moves.txt`, `twopileResults.txt` (see `MultipilePercentWin.java`, `TwoPileWinPercentIncrementor.java`).
- Parameters $(x,y)$, start lengths, steps, and accuracies are echoed to console for traceability.

---

## 12) Known Limitations (by design of this repo)

- This is **not** classic Nim with arbitrary removals; each pile uses a subtraction set $\langle 1,x_i,y_i\rangle$.
- Imperfect play is modeled as “optimal vs random” coin‑flip, not $\varepsilon$‑greedy over a value function.
- Python “ipynb” files are scripts; there is no saved notebook state.
