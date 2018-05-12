from sklearn import svm, datasets, cross_validation
from sklearn import metrics

iris = datasets.load_iris()

C = 1.0
svc = svm.SVC(kernel='linear', C=C)
rbf_svc = svm.SVC(kernel='rbf', gamma=0.7, C=C)
poly_svc = svm.SVC(kernel='poly', degree=3, C=C)
lin_svc = svm.LinearSVC(C=C)

svc.fit(iris.data,iris.target)
preds = svc.predict(iris.data)
accuracy = metrics.accuracy_score(iris.target, preds)
print(accuracy)

rbf_svc.fit(iris.data,iris.target)
preds = rbf_svc.predict(iris.data)
accuracy = metrics.accuracy_score(iris.target, preds)
print(accuracy)

poly_svc.fit(iris.data,iris.target)
preds = poly_svc.predict(iris.data)
accuracy = metrics.accuracy_score(iris.target, preds)
print(accuracy)

lin_svc.fit(iris.data,iris.target)
preds = lin_svc.predict(iris.data)
accuracy = metrics.accuracy_score(iris.target, preds)
print(accuracy)