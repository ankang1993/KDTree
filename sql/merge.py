# -*- coding: utf-8 -*-
"""
Created on Mon Sep 18 10:50:04 2017

@author: AK
"""

a = {0}
s = set(a)
f3 = open("C:\\Users\\delluser\\桌面\\final.txt","a")
with open("C:\\Users\\delluser\\桌面\\node.txt") as IN1, open("C:\\Users\\delluser\\桌面\\way_new.txt") as IN2:
	f1 = {k: v1 + " " + v2 for k, v1, v2, v3 in [l1.split('|') for l1 in IN1]}
	f2 = {k: v for k, v in [l2.split('|') for l2 in IN2]}
#	t = [l1.split('|') for l1 in IN1]
#	print(t)
for k in f1:
	if k in s:
		continue
	s.add(k)
	if k not in f2:
		f3.write(k + " " + f1[k] + " " + "0\n")
	else:
		f3.write(k + " " + f1[k] + " " + f2[k])
f3.close()