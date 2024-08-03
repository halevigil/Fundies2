arr=["trigramUS","trigramUS","trigramUAE","trigramUS","trigramZoo"]
def arr_to_list(arr):
    mylist=""
    layers=0
    for e in arr:
        mylist+="new ConsLoTrigram("+e+","
        layers+=1;
    mylist+="new MtLoTrigram()"
    for layer in range(layers):
        mylist+=")"
    return mylist
print(arr_to_list(arr))