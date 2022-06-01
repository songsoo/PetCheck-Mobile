import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
class dogBeat:

    def __init__(self,db_url,json_dir):

        cred = credentials.Certificate(json_dir)
        default_app = firebase_admin.initialize_app(cred, {'databaseURL': db_url})

        ref = db.reference('UserData')
        self.row = ref.get()

    def getState(self,state):

        def addArray(row, state, Arr):
            for user in row:
                for data in row[user][state].values():
                    a = list(map(float, data.split("/")[1:]))
                    Arr.append(a)

        Arr = []

        if(state=="Angry"):
            addArray(self.row, "State|Angry", Arr)
        elif(state=="Normal"):
            addArray(self.row, "State|Walk", Arr)
        elif(state=="Snack"):
            addArray(self.row, "State|Snack", Arr)

        return Arr


