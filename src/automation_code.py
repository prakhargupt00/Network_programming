import os
import csv
import schedule
import time
from datetime import datetime

#Taking the input 
print('Enter the IP address:')
ip = input()
print('Enter no.of probes:')
pb = input()
pb1=60/int(pb)

#creating the function for the automation
def command():
        os.system('cmd /c "nmap -n -sP" ' + str(ip) +'/24>output.txt')
        with open ('output.txt', 'rt') as myfile:
            contents = myfile.read().count("Host is up")
        with open('output.csv', 'a', newline='') as file:
            writer = csv.writer(file)
            writer.writerow([datetime.now(),contents])

#scheduling the command
schedule.every(pb1).minute.do(command)
while True:
    schedule.run_pending()
    time.sleep(1)