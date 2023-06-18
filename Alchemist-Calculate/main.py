
import time

#import numba


#@numba.jit(nopython=False)
def num():

    arr = []
    for i in range(1000000):
        arr.append(i/0.1346114249*0.2541369+0.215-3.20*0.2541*8.241*11.4514)



stime = time.time()
num()
etime = time.time() - stime
print(etime)
