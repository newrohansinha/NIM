# NIM Research Toolkit — Sprague–Grundy, Imperfect Play, Stabilization Analysis

> README generated on 2025-10-18. Focus: **math-first exposition** (symbols ≫ words).

---

## 0. Repo Map

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

## 1. Game Model (Nim)

**State**: \( \mathbf{h}=(h_1,\dots,h_k),\ h_i\in\mathbb{N}_0 \).  
**Move**: choose pile \(i\) and set \(h_i \leftarrow h_i' \in \{0,\dots,h_i-1\}\).  
**Terminal**: \( \mathbf{0} \) (no legal move).  
**Sum**: disjunctive sum \( \oplus \) over piles.

---

## 2. Sprague–Grundy Theory

**Impartial game \((\mathcal{P},\to)\)**.  
**Grundy** \( g:\mathcal{P}\to\mathbb{N}_0 \).

- Base: \( g(\mathbf{0})=0 \).
- Recurrence: \( g(x)=\mathrm{mex}\{\, g(y)\mid x\to y \,\} \).
- Disjunctive sum: \( g(x\oplus y)=g(x)\ \underline{\oplus}\ g(y) \) (bitwise XOR).
- **Nim**: \( g(\langle h\rangle)=h \). Hence for \( \mathbf{h}=(h_1,\dots,h_k) \)
  \[ g(\mathbf{h}) = h_1\ \underline{\oplus}\ h_2\ \underline{\oplus}\ \cdots\ \underline{\oplus}\ h_k. \]

**Winning criterion** (normal play): \( g(\mathbf{h})\neq 0 \iff \) next player has a win.

---

## 3. Optimal Move Construction (Closed Form)

Let \( X = h_1\ \underline{\oplus}\ \cdots\ \underline{\oplus}\ h_k \). If \( X=0 \) → no winning move. Else:

- Let \( b=\lfloor \log_2 X \rfloor \) (index of MSB of \(X\)).
- Choose any pile \(i\) with bit \(b\) set (\(h_i\ \&\ 2^b \neq 0\)).
- Set
  \[ h_i' = h_i\ \underline{\oplus}\ X \quad\text{with}\quad h_i' < h_i. \]
- New state has zero nim-sum:
  \[ (h_1\ \underline{\oplus}\ \cdots\ \underline{\oplus}\ h_i'\ \underline{\oplus}\ \cdots\ \underline{\oplus}\ h_k)=0. \]

Time: \(O(k)\).

---

## 4. Grundy DP (Finite Game Graph)

For a finite impartial game graph \(G=(V,E)\):

- Topological order (acyclic): compute \(g(v)\) by mex of children.
- With cycles: retrograde via memoization + DFS, or reduce to kernel of options if well-founded.

**Pseudo**

```
GRUNDY(v):
  if memo[v] defined: return memo[v]
  S = { GRUNDY(u) for (v→u) in E }
  g = mex(S)
  memo[v] = g
  return g
```

**mex(S)**: minimal \(m\in\mathbb{N}_0\) with \(m\notin S\).

---

## 5. Imperfect Play Models

Let \( \mathcal{A}(\mathbf{h}) \) = legal actions, \(a^\star\) optimal action reducing nim-sum to \(0\).

### 5.1 ε-greedy
\[
P(a\mid \mathbf{h}) =
\begin{cases}
1-\varepsilon + \frac{\varepsilon}{|\mathcal{A}(\mathbf{h})|}, & a=a^\star,\\[4pt]
\frac{\varepsilon}{|\mathcal{A}(\mathbf{h})|}, & a\neq a^\star.
\end{cases}
\]

### 5.2 Softmax by value gap
Let value proxy \( \Delta(\mathbf{h},a) = \) nim-sum drop \(= g(\mathbf{h}) - g(\mathbf{h}_a) \ge 0\).  
\[
P(a\mid \mathbf{h}) = \frac{\exp(\beta\,\Delta(\mathbf{h},a))}{\sum_{a'} \exp(\beta\,\Delta(\mathbf{h},a'))},\quad \beta>0.
\]

### 5.3 Logistic “optimality”
With scalar advantage \( \Delta^\star = \Delta(\mathbf{h},a^\star) - \max_{a\ne a^\star}\Delta(\mathbf{h},a) \):
\[
P(a^\star\mid \mathbf{h}) = \sigma(\alpha\,\Delta^\star) = \frac{1}{1+\exp(-\alpha\,\Delta^\star)}.
\]

---

## 6. Monte Carlo Simulator

**Goal**: estimate win rate \( \hat{p} \), length dist., policy head-to-head.

**Loop** (seeded RNG):
```
for episode=1..N:
  h ← init(k, heaps)
  player ← 0
  while h ≠ 0:
     choose a ~ P(a|h, player)       # ε-greedy / softmax / logistic
     h ← step(h, a)
     player ← 1 - player
  winner[player]++
```

- Complexity: \( O(N\cdot T) \) where \(T\)=avg. moves/episode.
- Variance: \( \operatorname{Var}(\hat{p}) \approx \frac{\hat{p}(1-\hat{p})}{N} \).  
  95% CI: \( \hat{p} \pm 1.96\sqrt{\hat{p}(1-\hat{p})/N} \).

---

## 7. Stabilization Analysis (Smoothing, Gradients, Curvature)

Let discrete surface \( S:\mathbb{Z}^2\to\mathbb{R} \) (e.g., performance metric over a 2‑param grid).

### 7.1 Gaussian smoothing
\[
G_\sigma(x,y) = \frac{1}{2\pi\sigma^2}\exp\!\left(-\frac{x^2+y^2}{2\sigma^2}\right),\quad
S_\sigma = G_\sigma * S.
\]
Discrete via separable kernels \(g_\sigma \otimes g_\sigma\).

### 7.2 Gradient and Laplacian
\[
\nabla S_\sigma = \begin{bmatrix}\partial_x S_\sigma \\ \partial_y S_\sigma\end{bmatrix},
\quad
\|\nabla S_\sigma\| = \sqrt{(\partial_x S_\sigma)^2 + (\partial_y S_\sigma)^2},
\quad
\Delta S_\sigma = \partial_{xx} S_\sigma + \partial_{yy} S_\sigma.
\]
Finite differences (e.g., Sobel/Scharr for \(\partial_x,\partial_y\)).

### 7.3 Hessian and principal curvatures
\[
H(S_\sigma)=\begin{bmatrix}
\partial_{xx} S_\sigma & \partial_{xy} S_\sigma\\
\partial_{xy} S_\sigma & \partial_{yy} S_\sigma
\end{bmatrix},
\quad \lambda_{1,2} = \text{eig}(H).
\]
Stability candidates: \( \|\nabla S_\sigma\| \le \tau_g \) and curvature test (e.g., \(\lambda_1<0,\lambda_2<0\) for maxima).

### 7.4 Scale-space persistence
For \( \sigma \in \{\sigma_1<\cdots<\sigma_m\} \), define persistence
\[
\Pi(p) = \#\{\sigma: p \text{ remains a critical point at scale } \sigma\}.
\]
Stable points: \( \Pi(p)\ge \tau_\Pi \).

### 7.5 1D time series (iteration index \(t\))
If \( S(t) \) (e.g., reward vs. episode):
- Smooth: \( S_\sigma = g_\sigma * S \).
- Stationarity: \( |S'_\sigma(t)| \le \tau \).
- Curvature: \( S''_\sigma(t) \approx 0 \) for plateaus; \( S''_\sigma(t)<0 \) near peaks.

**Discrete derivatives**
\[
S'_\sigma[t] \approx \tfrac{1}{2}(S_\sigma[t+1]-S_\sigma[t-1]),\quad
S''_\sigma[t] \approx S_\sigma[t+1]-2S_\sigma[t]+S_\sigma[t-1].
\]

---

## 8. Python Reference (Analysis Pipeline)

```
# core modules (typical)
nim/
  game.py            # state ops, nim-sum, optimal move
  policies.py        # ε-greedy, softmax(β), logistic(α)
  simulate.py        # Monte Carlo episodes with seeding
  grundy.py          # generic GRUNDY() DP on graphs
analysis/
  grid.py            # grid eval of policies / heap configs
  smooth.py          # Gaussian smoothing (separable conv)
  gradcurv.py        # ∇S, ΔS, Hessian, eig(2×2)
  stabilize.py       # thresholding τ_g, τ_κ, persistence Π
  plots.py           # PNG curves, heatmaps
```

**Complexities**
- Nim-sum, optimal move: \(O(k)\).
- One episode: \(O(T\cdot k)\).
- Grid eval \(n_x\times n_y\): \(O(n_x n_y)\) per metric per policy.
- Smoothing: separable \(O((n_x+n_y)\sigma)\) per row/col (approx).

---

## 9. Metrics

- Win rate \(p\); avg. moves \(\mathbb{E}[T]\).
- Advantage gap \(\Delta^\star\).
- Stabilization index \(\Pi\); gradient norm \(\|\nabla S_\sigma\|\) stats.
- Confidence bands via binomial or bootstrap.

---

## 10. Reproducibility

- RNG seeds fixed per trial.
- CSV logs: episodes, outcomes, lengths, policy params \((\varepsilon,\beta,\alpha)\).
- Deterministic plots (PNG) with captured config JSON.

---

## 11. CLI (example)

```
# simulate head-to-head (ε-greedy)
python -m nim.simulate --heaps 7 5 3 --episodes 100000 --epsilon 0.1

# grid-scan β for softmax
python -m analysis.grid --policy softmax --beta 0.1 5.0 --nx 64 --ny 64 --out grid.csv

# stabilization on reward curve
python -m analysis.stabilize --csv rewards.csv --sigma 3 --tau_g 1e-3 --tau_pi 2 --out stab.json
```

---

## 12. Mathematical Appendix

### A. mex
\[
\mathrm{mex}(S)=\min\{m\in\mathbb{N}_0: m\notin S\}.
\]

### B. XOR identities
\[
x\ \underline{\oplus}\ x=0,\quad x\ \underline{\oplus}\ 0=x,\quad (a\ \underline{\oplus}\ b)\ \underline{\oplus}\ c=a\ \underline{\oplus}\ (b\ \underline{\oplus}\ c).
\]

### C. Optimal reduction amount
Given \(X=\underline{\oplus}_i h_i\neq 0\), choose \(i\) with MSB\( (h_i) \) covering MSB\( (X)\):
\[
h_i' = h_i\ \underline{\oplus}\ X,\quad h_i' < h_i.
\]
Removed tokens \(r=h_i-h_i'\).

### D. Curvature classification via Hessian
- Max: \(\lambda_1<0,\lambda_2<0\).
- Min: \(\lambda_1>0,\lambda_2>0\).
- Saddle: \(\lambda_1\cdot\lambda_2<0\).

### E. Discrete Gaussian kernel (1D)
\[
g_\sigma[n] = \frac{1}{Z}\exp\!\left(-\frac{n^2}{2\sigma^2}\right),\quad Z=\sum_{n=-R}^{R}\exp\!\left(-\frac{n^2}{2\sigma^2}\right).
\]

### F. Plateaus (stabilization in 1D)
\[
|S'_\sigma[t]|\le \tau_g,\quad |S''_\sigma[t]|\le \tau_\kappa \ \Rightarrow\ \text{stable window}.
\]

---

## 13. Results Template (fill with your CSV/PNGs)

- \(N=10^6\) games per condition.
- Policies: ε∈{0.0,0.05,0.1}, β∈{0.5,1,2,4}, α∈{1,2,4}.
- Report: \(p\pm\)95% CI, \(\mathbb{E}[T]\), effect sizes, stabilization indices.

---

## 14. License / Citation

- Add `LICENSE` (MIT/Apache‑2.0) as appropriate.
- If publishing, cite Sprague–Grundy literature and your JEI/ACM SIGACT News manuscripts where relevant.
