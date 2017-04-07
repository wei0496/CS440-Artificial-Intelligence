'''
CS440 Programming Assignment 2
optimize.py
    Optimize the HMM and report the probabilities before and after optimization
    Using the Baum-Welch algorithm

Wei Wei wei0496@bu.edu
Teammates:
    Tianqi Xu
    Yehui Huang
    

2016/4/4
'''

import sys
import os

#Assumed input parameters from sys.argv would be:
#'optimize.py'
#'sentence.hmm'
#'example[x].obs'
#'filename.hmm'

#Error checking
if len(sys.argv) != 4:
    print 'Invalid inputs!'
    sys.exit()

#function that read info from 'sentence.hmm'
def sentence_info(filename):
    sentence = open(filename, 'r')
    lines = sentence.readlines()
    idx = 0
    
    # take N,M,T as input
    file2 = lines[idx]
    N = int(file2[0])
    M = int(file2[2])
    T = int(file2[4])
    
    idx += 1
    # read the states
    state = lines[idx]
    states = state.split()
    
    idx += 1
    # read the observations
    observation = lines[idx]
    observations = observation.split()
    
    idx += 1
    # read the matrix a,matrix b and pi
    A = []
    B = []
    Pi = []
    
    if lines[idx][0] == 'a':
        idx += 1
        for i in range(0,N):
            a = lines[idx].split()
            A.append(map(float, a))
            idx += 1
    
    if lines[idx][0] == 'b':
        idx += 1
        for i in range(0,N):
            b = lines[idx].split()
            B.append(map(float, b))
            idx += 1
    
    if lines[idx][0] == 'p':
        idx += 1
        pi = lines[idx].split()
        Pi.append(map(float, pi))
        idx += 1

    return observations, states, Pi, A, B
    
    
observations, states, Pi, A, B = sentence_info(sys.argv[1])
'''
print("observations")
print(observations)
print("states")
print(states)
print("start_probability")
print(Pi)
print("transition_probability")
print(A)
print("emission_probability")
print(B)
'''

#function that read info from 'example[x].obs'
def example_info(filename):
    example = open(filename, 'r')
    obs = example.readlines()
    idx = 0
    num_datasets = int(obs[idx])
    Observations = []
    
    idx += 1
    for i in range(0,num_datasets):
        num_o = obs[idx]
        idx += 1
        o = obs[idx].split()
        idx += 1
        Observations.append(o)
    return Observations

#e_obs is a list of observations from
e_obs = example_info(sys.argv[2])

#helper function that performs the forward operations
def forward(observations, states, Pi, A, B, example_obs, t, idx):
    #special case for update_Pi
    if t == 0:
        probability = Pi[0][idx] * B[idx][observations.index(example_obs[t])]
        return probability
    
    for x, y in enumerate(example_obs):
        #normal case
        f_curr = []
        state_index = observations.index(y)
        if x == t:
            #desired time reached, stop calculating
            
            #I move this before the for loop so a_(t-1) is computed
            #instead of a_(t), because otherwise there may be 
            #problem for the latter calculation of a_(t)
            break
        for j in range(0,len(states)):
            if x == 0:
                #initialization
                f_curr.append(Pi[0][j] * B[j][state_index])
            else:
                #induction
                g = B[j][state_index] * sum(f_prev[z] * A[z][j] for z in range(0,len(states)))
                f_curr.append(g)

        f_prev = f_curr
        
    # calculating the probability at time t
    state_index = observations.index(example_obs[t])
    probability = B[idx][state_index] * sum(f_prev[z] * A[z][idx] for z in range(0,len(states)))
    return probability

#helper function that performs the backward operations
def backward(observations, states, Pi, A, B, example_obs, t, idx):
    probability = 0
    if t+1 == len(example_obs):
        #base case for backward algorithm
        return 1
    else:
        #induction step
        for j in range(0,len(states)):
            state_index = observations.index(example_obs[t+1])
            pre_b = backward(observations, states, Pi, A, B, example_obs, t+1, j)
            probability += A[idx][j] * pre_b * B[j][state_index]
        return probability

#helper function that update Pi (Initial State Distribution)
def update_Pi(observations, states, Pi, A, B, example_obs, idx):
    denominator = 0
    x = forward(observations, states, Pi, A, B, example_obs, 0, idx)
    y = backward(observations, states, Pi, A, B, example_obs, 0,idx)
    numerator = x * y

    for i in range(0,len(states)):
        x1 = forward(observations, states, Pi, A, B, example_obs, 0, i)
        y1 = backward(observations, states, Pi, A, B, example_obs, 0, i)
        denominator += x1 * y1
    
    if denominator == 0:
        #for case when the denominator is 0, simply use the corresponding values from 
        #original hmm
        #print 'denominator 0 reached'
        return Pi[0][idx]

    return numerator / denominator

#helper function that calculates lambda
def lamda(observations, states, Pi, A, B, example_obs, t, i, j):
    denominator = 0
    numerator = 0
    
    x = forward(observations, states, Pi, A, B, example_obs, t, i)
    y = A[i][j]
    index = observations.index(example_obs[t+1])
    x1 = B[j][index]
    y1 = backward(observations, states, Pi, A, B, example_obs, t+1, j)
    numerator = x * y * x1 * y1
    
    for i1 in range(0,len(states)):
        for j1 in range(0,len(states)):
            x = forward(observations, states, Pi, A, B, example_obs, t, i1)
            y = A[i1][j1]
            index = observations.index(example_obs[t+1])
            x1 = B[j1][index]
            y1 = backward(observations, states, Pi, A, B, example_obs, t+1, j1)
            denominator += x * y * x1 * y1
    if denominator == 0:
        return 0
    return numerator / denominator

#helper function that calculates gamma
def gamma(observations, states, Pi, A, B, example_obs, t, i):
    denominator = 0
    x = forward(observations, states, Pi, A, B, example_obs, t, i)
    y = backward(observations, states, Pi, A, B, example_obs, t, i)
    numerator = x * y
    for i_1 in range(0,len(states)):
        x = forward(observations, states, Pi, A, B, example_obs, t, i_1)
        y = backward(observations, states, Pi, A, B, example_obs, t, i_1)
        denominator += x * y
    if denominator == 0:
        return 0
    return numerator / denominator

#helper function that update A
def update_A(observations, states, Pi, A, B, example_obs, i, j):
    denominator = 0
    numerator = 0
    for t in range(0,len(example_obs)-1):
        numerator += lamda(observations, states, Pi, A, B, example_obs, t, i, j)
    for t in range(0,len(example_obs)-1):
        denominator += gamma(observations, states, Pi, A, B, example_obs, t, i)     
    if denominator == 0:
        #print 'denominator 0 reached, A'
        return A[i][j]
    return numerator / denominator

#helper function that updates B
def update_B(observations, states, Pi, A, B, example_obs, j, k):
    denominator = 0
    numerator = 0
    
    for t in range(0,len(example_obs)):
        ga = gamma(observations, states, Pi, A, B, example_obs, t, j)
        denominator += ga
    
    for t in range(0,len(example_obs)):
        if example_obs[t] == observations[k]:
            ga1 = gamma(observations, states, Pi, A, B, example_obs, t, j)
            numerator += ga1
    if denominator == 0:
        #print 'den 0 rached, B'
        return B[j][k]
    return numerator / denominator

#function that performs the re-estimation process
def re_estimation(states, e_obs):
    for o in e_obs:
        #Update matrix Pi (Initial State Distribution)
        Updated_Pi = []
        for i in range(0,len(states)):
            probability = update_Pi(observations, states, Pi, A, B, o, i)
            Updated_Pi.append(probability)

        #Update matrix A (Transition Matrix)
        Updated_A = []
        for i in range(0,len(states)):
            update = []
            for j in range(0,len(states)):
                z = update_A(observations, states, Pi, A, B, o, i, j)
                update.append(z)
            Updated_A.append(update)
        
        #Update matrix B (Observation Probability Matrix)
        Updated_B = []
        for j in range(0,len(states)):
            update = []
            for k in range(0,len(observations)):
                z = update_B(observations, states, Pi, A, B, o, j, k)
                update.append(z)
            Updated_B.append(update)

    return Updated_Pi, Updated_A, Updated_B

Updated_Pi, Updated_A, Updated_B = re_estimation(states, e_obs)
Updated_Pi = [Updated_Pi]

#helper function that calculates the probability
def prob(observations, states, Pi, A, B, example_obs):
    for x, y in enumerate(example_obs):
        f_curr = []
        state_index = observations.index(y)
        for j in range(0,len(states)):
            if x == 0:
                #initialization
                f_curr.append(Pi[0][j] * B[j][state_index])
            else:
                #induction
                g = B[j][state_index] * sum(f_prev[z] * A[z][j] for z in range(0,len(states)))
                f_curr.append(g)

        f_prev = f_curr
        
    return sum(f_curr)
    
for o in e_obs:
    print prob(observations, states, Pi, A, B, o), prob(observations, states, Updated_Pi, Updated_A, Updated_B, o)

#function that write outputs to 'filename.hmm'
def write_to(filename, states, observations, A, B, Pi):
    f = open(filename, 'w')
    
    N = len(states)
    M = len(observations)
    T = max(len(o) for o in e_obs)
    f.write(' '.join(map(str, [N,M,T])))
    
    f.write('\n' + ' '.join(states))
    f.write('\n' + ' '.join(observations))
    f.write('\na:')
    for l in A:
        f.write('\n' + ' '.join(map(str, l)))
    f.write('\nb:')
    for l in B:
        f.write('\n' + ' '.join(map(str, l)))
    f.write('\npi:')
    for l in Pi:
        f.write('\n' + ' '.join(map(str, l)))

write_to(sys.argv[3], states, observations, Updated_A, Updated_B, Updated_Pi)