# NIM Research Toolkit — Sprague–Grundy, Imperfect Play, Stabilization Analysis

README generated on 2025-10-18. **Math‑first** (symbols ≫ words). GitHub‑compatible math: `$...$` inline, `$$...$$` display.

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

## 1. Game Model

Inline state, move, terminal, sum:

$\mathbf h=(h_1,\dots,h_k)\in\mathbb N_0^k$; move: pick $i$ and set $h_i'\in\{0,\dots,h_i-1\}$; terminal $\mathbf 0$; sum $\oplus$.

---

## 2. Sprague–Grundy

Base $g(\mathbf 0)=0$. Recurrence $g(x)=\mathrm{mex}\{g(y):x\to y\}$. Sum $g(x\oplus y)=g(x)\oplus g(y)$.  
Nim piles: $g(\langle h\rangle)=h$. Hence

$$
g(\mathbf h)=h_1\oplus h_2\oplus\cdots\oplus h_k.
$$

Win (normal play) iff $g(\mathbf h)\ne 0$.

---

## 3. Optimal Move (Closed Form)

Let $X=h_1\oplus\cdots\oplus h_k$. If $X=0$ no win. Else choose $i$ with MSB$(h_i)$ covering MSB$(X)$ and set

$$
h_i' = h_i\oplus X,\qquad h_i' < h_i,
$$

then

$$
h_1\oplus\cdots\oplus h_i'\oplus\cdots\oplus h_k=0.
$$

Time $O(k)$.

---

## 4. Grundy DP (finite graph)

Pseudo:

```
GRUNDY(v):
  if memo[v] defined: return memo[v]
  S = { GRUNDY(u) for (v→u) in E }
  g = mex(S)
  memo[v] = g
  return g
```

$\mathrm{mex}(S)=\min\{m\in\mathbb N_0\mid m\notin S\}$.

---

## 5. Imperfect Play Models

Let $\mathcal A(\mathbf h)$ actions; $a^\star$ optimal.

**ε‑greedy**

$$
P(a\mid\mathbf h)=
\begin{cases}
1-\varepsilon+\dfrac{\varepsilon}{\lvert\mathcal A(\mathbf h)\rvert}, & a=a^\star,\\[6pt]
\dfrac{\varepsilon}{\lvert\mathcal A(\mathbf h)\rvert}, & a\ne a^\star.
\end{cases}
$$

**Softmax (gap)** with $\Delta(\mathbf h,a)=g(\mathbf h)-g(\mathbf h_a)\ge 0$

$$
P(a\mid\mathbf h)=
\dfrac{\exp(\beta\,\Delta(\mathbf h,a))}{\sum\limits_{a'\in\mathcal A(\mathbf h)} \exp(\beta\,\Delta(\mathbf h,a'))},\quad \beta>0.
$$

**Logistic “optimality”** with $\Delta^\star=\Delta(\mathbf h,a^\star)-\max_{a\ne a^\star}\Delta(\mathbf h,a)$

$$
P(a^\star\mid\mathbf h)=\sigma(\alpha\,\Delta^\star)=\frac{1}{1+\exp(-\alpha\,\Delta^\star)}.
$$

---

## 6. Monte Carlo

Goal: $\hat p$, length, head‑to‑head. Complexity $O(NT)$. CI: $\hat p\pm 1.96\sqrt{\hat p(1-\hat p)/N}$.

```
for ep in 1..N:
  h ← init(k, heaps)
  p ← 0
  while h ≠ 0:
     a ~ P(a|h, policy[p])
     h ← step(h,a)
     p ← 1-p
```

---

## 7. Stabilization (smoothing, ∇, κ)

Let grid surface $S:\mathbb Z^2\to\mathbb R$.

**Gaussian**

$$
G_\sigma(x,y)=\frac{1}{2\pi\sigma^2}\exp\!\left(-\frac{x^2+y^2}{2\sigma^2}\right),\quad
S_\sigma=G_\sigma*S.
$$

**Gradient / Laplacian**

$$
\nabla S_\sigma=\begin{bmatrix}\partial_x S_\sigma\\ \partial_y S_\sigma\end{bmatrix},\quad
\lVert\nabla S_\sigma\rVert=\sqrt{(\partial_x S_\sigma)^2+(\partial_y S_\sigma)^2},\quad
\Delta S_\sigma=\partial_{xx} S_\sigma+\partial_{yy} S_\sigma.
$$

**Hessian / eigenvalues**

$$
H=\begin{bmatrix}
\partial_{xx}S_\sigma & \partial_{xy}S_\sigma \\
\partial_{xy}S_\sigma & \partial_{yy}S_\sigma
\end{bmatrix},\quad \lambda_{1,2}=\mathrm{eig}(H).
$$

Stable if $\lVert\nabla S_\sigma\rVert\le \tau_g$ and, e.g., maxima when $\lambda_1<0,\lambda_2<0$.

**Scale‑space persistence** for $\sigma\in\{\sigma_1<\cdots<\sigma_m\}$

$$
\Pi(p)=\#\{\sigma: p\text{ remains critical at scale }\sigma\}.
$$

**1D time series** $S(t)$

$$
S'_\sigma[t]\approx \tfrac12\big(S_\sigma[t+1]-S_\sigma[t-1]\big),\qquad
S''_\sigma[t]\approx S_\sigma[t+1]-2S_\sigma[t]+S_\sigma[t-1].
$$

Plateau window: $\lvert S'_\sigma[t]\rvert\le \tau_g$ and $\lvert S''_\sigma[t]\rvert\le \tau_\kappa$.

---

## 8. Python Analysis Modules (ref)

```
nim/
  game.py           # nim-sum, optimal move
  policies.py       # ε-greedy, softmax(β), logistic(α)
  simulate.py       # Monte Carlo
  grundy.py         # generic GRUNDY()
analysis/
  grid.py           # grid eval
  smooth.py         # Gaussian smoothing
  gradcurv.py       # ∇, Δ, Hessian
  stabilize.py      # thresholds τ_g, τ_κ, persistence Π
  plots.py          # PNG
```

Complexities: nim-sum/optimal $O(k)$; episode $O(Tk)$; grid $O(n_xn_y)$; separable smooth ~ $O((n_x+n_y)\,R)$ kernel radius $R\sim 3\sigma$.

---

## 9. Metrics

$p$, $\mathbb E[T]$, gap $\Delta^\star$, $\Pi$, $\lVert\nabla S_\sigma\rVert$.

---

## 10. Reproducibility

Seeds; CSV logs; PNG plots; config JSON. Deterministic pipelines.

---

## 11. CLI (examples)

```
python -m nim.simulate --heaps 7 5 3 --episodes 100000 --epsilon 0.1
python -m analysis.grid --policy softmax --beta 0.1 5.0 --nx 64 --ny 64 --out grid.csv
python -m analysis.stabilize --csv rewards.csv --sigma 3 --tau_g 1e-3 --tau_pi 2 --out stab.json
```

---

## 12. Appendix

**mex**: $\mathrm{mex}(S)=\min\{m\in\mathbb N_0\mid m\notin S\}$.  
**XOR laws**: $x\oplus x=0$, $x\oplus 0=x$, associativity/commutativity.  
**Optimal reduction**: $X\ne0$, choose $i$ with MSB match, set $h_i'=h_i\oplus X<h_i$.  
**Gaussian 1D**: $g_\sigma[n]=Z^{-1}\exp\big(-n^2/(2\sigma^2)\big)$, $Z=\sum_{n=-R}^R\exp(-n^2/(2\sigma^2))$.

---

## 13. Results Template

$N=10^6$ per condition. Policies: $\varepsilon\in\{0,0.05,0.1\}$, $\beta\in\{0.5,1,2,4\}$, $\alpha\in\{1,2,4\}$. Report $p\pm95\%$ CI, $\mathbb E[T]$, effect sizes, $\Pi$.
