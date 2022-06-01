import os
import sys

import numpy as np
from scipy.ndimage import filters
import matplotlib.pyplot as plt
from scipy.signal.windows import gaussian
from beatData import dogBeat

if __name__ == '__main__':

    if len(sys.argv) < 3:
        print(sys.argv[1])
        print('Usage: {} <json directory> <db_url>'.format(sys.argv[0]))
        sys.exit(1)

    json_dir = str(sys.argv[2])
    db_url = str(sys.argv[1])


    dogBeat2 = dogBeat(db_url,json_dir)

    Angry = dogBeat2.getState("Angry")
    Normal = dogBeat2.getState("Normal")
    Snack = dogBeat2.getState("Snack")

    time_series_value_amount = 10

    length = 10

    x = np.linspace(0, length, time_series_value_amount)

    ang_avg = np.zeros(10)
    nor_avg = np.zeros(10)
    snk_avg = np.zeros(10)

    b = gaussian(10, 3)

    def getAverage(val_arr,avg_arr, color,label):
        for val in val_arr:
            plt.plot(x, val, ls='none', marker='.', color=color)
            avg_arr += val
        avg_arr /= len(val_arr)
        avg_arr = filters.convolve1d(avg_arr, weights=b / b.sum())
        plt.plot(x,avg_arr, color=color,label=label)
        return avg_arr

    ang_avg = getAverage(Angry,ang_avg,'r','Angry')
    nor_avg = getAverage(Normal,nor_avg,'g','Normal')
    #snk_avg = getAverage(Snack,snk_avg,'b')
    plt.legend()

    plt.show()

