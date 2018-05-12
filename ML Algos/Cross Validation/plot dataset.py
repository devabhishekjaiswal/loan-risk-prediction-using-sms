import pandas as pd
import numpy as np
from sklearn.feature_extraction import DictVectorizer
from sklearn import tree
from sklearn import metrics
from sklearn import cross_validation
import matplotlib.pyplot as plt
from inspect import getmembers
from mpl_toolkits.mplot3d import Axes3D

df = pd.read_json('Reduced.json', orient='list')
# temp = df[15:]
# print temp[['credited', 'debited', 'rechargeAmount', 'label']]

# print(X)

target = []

# maxm = df['credited'].max()
#
# for index, elm in enumerate(df['credited']):
#     df['credited'][index] = df['credited'][index] / maxm
#
# for index, elm in enumerate(df['credited']):
#     print elm


for label in df['label']:
    if label == 'NO':
        target.append(0)
    else:
        target.append(1)

print len(target)
# print df.columns
dfdata = df[['credited', 'debited', 'rechargeAmount']]


fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

# avg = sum(df['rechargeAmount'])/len(df['rechargeAmount'])
# print avg

for a, b, c, d in zip(df['credited'], df['debited'], df['rechargeAmount'], df['label']):
    # print a, b, c, d
    if d == 'YES':
        ax.scatter(a, b, c, s=30, c='r')
    else:
        ax.scatter(a, b, c, c='b')
# ax.scatter([1], [1], [1], c='r')
# ax.scatter([2], [2], [2], c='b')
ax.set_xlabel('debited')
ax.set_ylabel('credited')
ax.set_zlabel('rechargeAmount')
plt.show()
