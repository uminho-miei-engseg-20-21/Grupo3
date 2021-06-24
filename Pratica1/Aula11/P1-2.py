import re

def ask_pay_amount():
    v = input("Valor a pagar:")
    try:
        amount = int(v)
    except ValueError:
        print("input inválido, tente novamente")
        return ask_pay_amount()
    if(amount < 0):
        print("input inválido, tente novamente")
        return ask_pay_amount()
    return amount

def ask_birthday():
    v = input("Data de Nascimento (DD/MM/AAAA): ")
    if( not re.match("^[0-9]{2}\/[0-9]{2}\/[0-9]{4}$",v) ):
        print("input inválido,tente novamente")
        return ask_birthday()
    else:
        data = v.split("/")
        dia = int(data[0])
        mes = int(data[1])
        ano = int(data[2])
        if(dia < 1 or dia>31 or mes<1 or mes>12 or ano<0):
            print("input inválido,tente novamente")
            return ask_birthday()
        return data

def ask_name():
    v = input("Número de identificação fiscal:")
    if( not re.match("^[A-Za-z ]*$",v) ):
        print("input inválido,tente novamente")
        return ask_name()
    return v

def ask_tax_number():
    v = input("Número de identificação fiscal:")
    if( not re.match("^[0-9]{9}$",v) ):
        print("input inválido,tente novamente")
        return ask_tax_number()
    if not v.isdigit() or len(v) != 9:
        print("input inválido,tente novamente")
        return ask_tax_number()
    soma = sum([int(dig) * (9 - pos) for pos, dig in enumerate(v)])
    resto = soma % 11
    if (v[-1] == '0' and resto == 1):
        resto = (soma + 10) % 11
    if (resto == 0):
        return v
    else:
        print("input inválido,tente novamente")
        return ask_tax_number()

def convertToNumber(l):
    charDict = {
        "0" : "0",
        "1" : "1",
        "2" : "2",
        "3" : "3",
        "4" : "4",
        "5" : "5",
        "6" : "6",
        "7" : "7",
        "8" : "8",
        "9" : "9",
        "A" : "10",
        "B" : "11",
        "C" : "12",
        "D" : "13",
        "E" : "14",
        "F" : "15",
        "G" : "16",
        "H" : "17",
        "I" : "18",
        "J" : "19",
        "K" : "20",
        "L" : "21",
        "M" : "22",
        "N" : "23",
        "O" : "24",
        "P" : "25",
        "Q" : "26",
        "R" : "27",
        "S" : "28",
        "T" : "29",
        "U" : "30",
        "V" : "31",
        "W" : "32",
        "X" : "33",
        "Y" : "34",
        "Z" : "35",
    }
    return int(charDict[l])

def ask_citizen_card_number():
    v = input("Número do cartão de cidadão(DDDDDDDDD C AAT):")
    if ( not re.match("^[0-9]{8}\ [0-9]\ [A-Z0-9]{2}[0-9]$",v) ):
        print("input inválido,tente novamente")
        return ask_citizen_card_number()
    sum = 0
    secondDigit = False
    number = v.replace(" ","")
    if (len(number) != 12):
        print("input inválido,tente novamente")
        return ask_citizen_card_number()
    for i in range(len(number)-1,-1,-1):
        valor = convertToNumber(number[i])
        if (secondDigit):
            valor *= 2
            if (valor > 9):
                valor -= 9
        sum += valor
        secondDigit = not secondDigit
    if( (sum%10) == 0 ):
        return number
    else:
        print("here input inválido,tente novamente")
        return ask_citizen_card_number()

def ask_credit_card_number():
    v = input("Número do cartão de crédito(DDDD DDDD DDDD DDDD):")
    if ( not re.match("^[0-9]{4}\ [0-9]{4}\ [0-9]{4}\ [0-9]{4}$",v) ):
        print("input inválido,tente novamente")
        return ask_credit_card_number()
    card = v.replace(" ","")
    nDigits = len(card)
    checksum = int(card[nDigits-1])
    sum = 0
    isSecondD = True
     
    for i in range(nDigits - 2, -1, -1):
        d = int(card[i])
     
        # Double every second digit, from the rightmost:
        if (isSecondD):
            d = d * 2
        
        if (d>9):
            d = d-9
  
        sum += d

        isSecondD = not isSecondD
     
    if ((sum+checksum) % 10 == 0):
        return card
    else:
        print("input inválido,tente novamente")
        return ask_credit_card_number()

def ask_credit_card_date():
    v = input("Data de Validade(MM/AA):")
    if ( not re.match("^[0-9]{2}/[0-9]{2}$",v) ):
        print("input inválido,tente novamente")
        return ask_credit_card_date()
    
    if ( v[0] == 0 ):
        mes = int(v[1])
    else:
        mes = int(v[0]+v[1])

    if ( v[3] == 0 ):
        ano = int(v[4])
    else:
        ano = int(v[3]+v[4])

    if(mes > 12 or mes < 1):
        print("input inválido,tente novamente")
        return ask_credit_card_date()

    if( ano<0 ):
        print("input inválido,tente novamente")
        return ask_credit_card_date()

    return v

def ask_credit_card_CVC():
    v = input("CVC/CVV(DDD):")
    if ( not re.match("^[0-9]{3}$",v) ):
        print("input inválido,tente novamente")
        return ask_credit_card_CVC()
    return v


ask_pay_amount()
ask_birthday()
ask_name()
ask_tax_number()
ask_citizen_card_number()
ask_credit_card_number()
ask_credit_card_date()
ask_credit_card_CVC()

