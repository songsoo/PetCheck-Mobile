import sys

import pandas as pd
from matplotlib import pyplot as plt

from beatData import dogBeat
import numpy as np
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

    time_series_value_amount = 0
    length = 0

    def moving_average(x, w):
        return np.convolve(x, np.ones(w), 'valid') / w

    def get_smoothed(np_array,color,label):
        np_array = np.array(np_array)
        smoothed_array = []

        for i,np_Angry_list in enumerate(np_array):
            smoothed_array.append(moving_average(np_Angry_list,5))

        smoothed_np_array = np.array(smoothed_array)

        pd_array = pd.DataFrame(smoothed_np_array)
        pd_smoothed_array = []

        for i in range(len(pd_array.columns)):
            pd_smoothed_array.append(pd_array.loc[:,i].ewm(span=20, adjust=False).mean())

        plt.plot(pd_smoothed_array, ls='none', marker='.', color=color)

        df_smoothed_array = pd.DataFrame(pd_smoothed_array)
        df_smoothed_array['avg'] = df_smoothed_array.mean(axis='columns')

        return df_smoothed_array['avg']


    smoothed_Angry = get_smoothed(Angry,'r','Angry')
    smoothed_Normal =get_smoothed(Normal,'g','Normal')

    plt.plot(smoothed_Angry,'r',label='Angry')
    plt.plot(smoothed_Normal,'g',label='Normal')
    plt.legend()

    plt.show()
