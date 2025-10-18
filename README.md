# NIM Research Toolkit — Sprague–Grundy, Imperfect Play, Stabilization Analysis

README generated on 2025-10-18. Math-first. **GitHub math uses `$...$` (inline) and `$$...$$` (block).**  
If you ever see raw TeX: view on github.com (not “Raw” or the mobile app).

---

## 0) Repo Map
```
📄 
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

## 1) Game rules (as in this codebase)

- **Single pile subtraction**: move set $M=\{1,x,y\}$. From $n\in\mathbb{N}_0$, legal: $n-1,n-x,n-y\ge 0$.  
  (Files: `GrundyNumberTableGenerator.java`, `Strategy*_1D.java`, `AccuracyCombinations_1D.java`, `compareAccuracyChangingSticks.java`)
- **Two/multi‑pile sum**: independent subtraction piles (per‑pile $(x_i,y_i)$). Overall Grundy is XOR.  
  (Files: `MultiPileOptimal.java`, `MultiOptimalMoveFinder.java`, `TwoPileWinPercentIncrementor.java`, `MultipilePercentWin.java`)
- **Imperfect play**: player accuracy $a\in[0,1]$. With prob. $a$ take **optimal** move; with prob. $1-a$ choose a **uniform random valid** move (bounded by $\max(1,x_i,y_i)$).

---

## 2) Sprague–Grundy for $\langle 1,x,y\rangle$

Single pile Grundy $g(n)$:
$$
g(0)=0,\qquad
g(n)=\mathrm{mex}\big\{ g(n-1),\ g(n-x),\ g(n-y) \big\}
\quad(\text{ignore negative indices}).
$$

Multi‑pile with sizes $n_1,\dots,n_k$ (per‑pile $x_i,y_i$):
$$
G\;=\;g_1(n_1)\oplus g_2(n_2)\oplus\cdots\oplus g_k(n_k),\qquad
\text{win iff }G\ne 0.
$$

---

## 3) DP tables used

`winTable[n]` in Java:
```
winTable[0] = 2  # losing for player to move
for n=1..N:
  winTable[n] = 2
  if n-1 >= 0 and winTable[n-1] == 2: winTable[n] = 1
  if n-x >= 0 and winTable[n-x] == 2: winTable[n] = 1
  if n-y >= 0 and winTable[n-y] == 2: winTable[n] = 1
```
(Where `1`=winning, `2`=losing.)

---

## 4) Optimal moves implemented

- **Single pile**: choose smallest $m\in M$ with $n-m\ge 0$ and `winTable[n-m]==2`. If none, pick any valid.
- **Multi pile**: compute per‑pile $g_i(n_i)$; choose pile $i$ and move $m\in M_i$ so new XOR is zero:
$$
g_i(n_i-m)=g_i(n_i)\oplus G,\qquad n_i-m\ge 0.
$$
Tie‑breakers: minimal removal or random among minima (per `MultipilePercentWin.java`).

---

## 5) Imperfect play (exactly as coded)

On a turn with accuracy $a$:
$$
P(\text{optimal})=a,\qquad P(\text{random valid})=1-a.
$$
Random removal on chosen pile is uniform in $\{1,\dots,\min(n_i,\max(1,x_i,y_i))\}$.

---

## 6) Multiple optimal subtractions (`MultiOptimalMoveFinder.java`)

For $(x,y)$ and pile size $n$:
$$
\text{two optimal moves}\iff
\big( n-x\ge 0\land \text{winTable}[n-x]=2 \big)\ \land\
\big( n-y\ge 0\land \text{winTable}[n-y]=2 \big).
$$

---

## 7) Stabilization (Python)

### 7.1 Single‑pile (`SinglePileStabilzationPointFinder.ipynb`)
Moving‑average smoothing (window $w$) and central‑difference gradient:
$$
\tilde S[i]=\frac1w\sum_{j=i-w+1}^{i}S[j],\qquad
S'[i]\approx\frac{\tilde S[i+1]-\tilde S[i-1]}{2\,\Delta x}.
$$
Stabilization index $t$ (first index with “flat enough” derivative range):
$$
\max_{u\in[t,T]}\lvert S'[u]\rvert-\min_{u\in[t,T]}\lvert S'[u]\rvert<\tau,
$$
with code default `derivative_threshold = 0.06`.

### 7.2 Two‑pile (`TwoPileStabilizationPointFinder.ipynb`)
Gaussian smoothing (SciPy) and gradient magnitude on a grid of pile sizes:
$$
Z_\sigma=G_\sigma*Z,\quad
G_\sigma(x,y)=\frac{1}{2\pi\sigma^2}\exp\!\Big(-\frac{x^2+y^2}{2\sigma^2}\Big),\quad
\lVert\nabla Z_\sigma\rVert=\sqrt{(\partial_x Z_\sigma)^2+(\partial_y Z_\sigma)^2}.
$$
Pick $(x^*,y^*)$ as an argmin of $\lVert\nabla Z_\sigma\rVert$ inside the window, subject to a small‑gradient threshold.

---

## 8) Complexity
Single‑pile table $O(N)$; per‑pile Grundy $O(N)$; multi‑pile optimal $O(k)$ once $g_i$ known; one simulated game $O(Tk)$.

---

## 9) Build & Run

**Java (JDK 17+)**
```
javac *.java

# Single‑pile accuracy vs optimal
java Strategy1

# Two‑pile sweep + win rates
java TwoPileWinPercentIncrementor

# Multi‑pile Monte‑Carlo
java MultipilePercentWin

# Find positions with two optimal subtractions
java MultiOptimalMoveFinder
```

**Python (analysis)**
```
python SinglePileStabilzationPointFinder.ipynb
python TwoPileStabilizationPointFinder.ipynb
```
(These `.ipynb` files are plain Python scripts in this repo.)

---

## 10) Appendix
$\mathrm{mex}(S)=\min\{m\in\mathbb N_0\mid m\notin S\}$.  
XOR: $a\oplus a=0$, $a\oplus 0=a$, associativity, commutativity.