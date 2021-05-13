vals = [None]*10
count = input("Quantos valores quer guardar no array? ")
count = int(count)
for i in range(0, int(count)):
    vals[i] = count-i
which = input("Que valor deseja recuperar? ")
print(vals)
print ('O valor e ', str(vals[int(which)]))

