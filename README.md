# Orbital Simulator
A Java-based Orbital Simulator built with LibGDX graphics libraries.

## Physics
This program attempts to simulate N-body simulations through continuous iterations by using Cowell's method, where acceleration is calculated for each body based on its attraction to other bodies, and this is used as a basis to calculate the velocity and position vectors.

### Numerical Integration
The orbital simulator can change between three types of numerical integration algorithms in order to iterate position and velocity values from acceleration and previous values.

#### Euler's Method
The basic, first-order method used to integrate. Trajectories are less accurate, but are faster to compute.

#### Range-Kutta 4 (RK4)
A fourth-order method based off of Euler's method that creates more accurate orbital paths.

#### Leapfrog (Velocity Verlet)
Second-order and symplectic. Being second-order, it is more accurate than Euler's method, but less accurate than RK4. However, it is symplectic, which means that it conserves the Hamiltonian (or total energy). It is less prone to systematic errors in the long-term as non-symplectic methods like Euler's and RK4.

## Interface
### Controls




### 
