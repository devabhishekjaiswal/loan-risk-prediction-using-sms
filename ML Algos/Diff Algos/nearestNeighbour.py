import pandas as pd
import numpy as np
from sklearn.feature_extraction import DictVectorizer
from sklearn import tree
from sklearn.datasets import load_iris
from sklearn import metrics
from sklearn.neighbors import KNeighborsClassifier
import matplotlib.pyplot as plt
from matplotlib.colors import ListedColormap


# iris = load_iris()
# print iris.data


df = pd.read_json('Reduced.json', orient='list')
# df = df[:, :2]
# print df[:, 0]
target = []

for label in df['label']:
    if label == 'NO':
        target.append(0)
    else:
        target.append(1)

print len(target)

dfdata = df[['credited', 'debited','rechargeAmount']]
# print dfdata
target_Train = target[:450]
Train = dfdata[:450]



neigh = KNeighborsClassifier(n_neighbors=3)
neigh.fit(Train, target_Train)
# rbf_svc = svm.SVC(kernel='rbf', gamma=0.5, C=C).fit(Train, target_Train)


# ------------------------------------------------------------------------------------------------------------
x_min, x_max = dfdata['credited'].min(), dfdata['credited'].max() + 1
y_min, y_max = dfdata['debited'].min(), dfdata['debited'].max() + 1
# z_min, z_max = df[:, 2].min() - 1, df[:, 2].max() + 1

print x_min, x_max
print y_min, y_max

h = 100
cmap_light = ListedColormap(['#FFAAAA', '#AAFFAA', '#AAAAFF'])
cmap_bold = ListedColormap(['#FF0000', '#00FF00', '#0000FF'])


temp = df[['credited', 'debited']]

xx, yy = np.meshgrid(np.arange(x_min, x_max, h), np.arange(y_min, y_max, h))
# print temp[200:]
Z = neigh.predict(np.c_[xx.ravel(), yy.ravel()])
# Put the result into a color plot
# Z = Z.reshape(xx.shape)
plt.figure()
plt.pcolormesh(xx, yy, Z, cmap=cmap_light)

# Plot also the training points
plt.scatter(dfdata['credited'], dfdata['debited'], c=target, cmap=cmap_bold)
plt.xlim(xx.min(), xx.max())
plt.ylim(yy.min(), yy.max())
plt.title("3-Class classification (k = %i)"
          % 3)

plt.show()
# -------------------------------------------------------------------------------------------------------
print 'here'
test = dfdata[450:]
# print test
# for i, clf in enumerate((svc, lin_svc, rbf_svc, poly_svc)):
# clf = rbf_svc

# print i
weight = []
for x in target[450:]:
    if x == 1:
        weight.append(5)
    else:
        weight.append(1)

Z = neigh.predict(test)
print Z[:]
print target[450:]
accuracy = metrics.accuracy_score(target[450:], Z)
print accuracy
print neigh.score(test, target[450:], weight)
