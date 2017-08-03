from flask import Flask, render_template ,jsonify, request
from flask_socketio import SocketIO, send, emit, join_room, leave_room
import datetime, time

app = Flask(__name__)
app.config['SECRET_KEY'] = 'secretkey'
socketio = SocketIO(app)


#Global variable to store the current state of the server
OnlineUserCount=-1
#OnlineUserList=[]

def GenerateJsonstatus(status):
	ts = time.time()
	st=datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')
	#return jsonify(OnlineUserCount=OnlineUserCount, #OnlineUserList=OnlineUserList, 
				#Status=status, TimeStamp=st)
	return {'OnlineUserCount':OnlineUserCount, 'Status':status, 'TimeStamp':st}			


@app.route('/')
def errorlogging():
    #return "OnlineUserCount:" + str(OnlineUserCount)
    #return render_template('hello.html', name=name)
    return render_template('websocket.log')
    
@app.route('/count')
def onlineusercounter():
    return "OnlineUserCount:" + str(OnlineUserCount)    


# Will be invoked whenever a client is connected
@socketio.on('connect')
def connect_handler():
	global OnlineUserCount
	OnlineUserCount+=1
	status = 'A new client is connected.'
	print(status)
	emit('event_server_status', GenerateJsonstatus(status), broadcast=True)

# Will be invoked whenever a client is disconnected
@socketio.on('disconnect')
def disconnect_handler():
	global OnlineUserCount
	OnlineUserCount-=1
	status = 'A client is disconnected.'
	print(status)
	emit('event_server_status', GenerateJsonstatus(status), broadcast=True)
	
	
# Will be invoked when the client draw a "DrawingObject"
@socketio.on('event_draw')
def event_draw_handler(json):
	print ("event_draw is triggered by user_id %s\nJson content: %s" % (json['user_id'], str(json)))
	emit('event_draw', json, broadcast=True)
	print ("event_draw is broadcasted to all online client!")
	
	
# Will be invoked when the client try to undo his/her drawing parts
@socketio.on('event_undo')
def event_undo_handler(json):
	print ("event_undo is triggered by user_id %s\nJson content: %s" % (json['user_id'], str(json)))
	emit('event_undo', json, broadcast=True)
	print ("event_undo is broadcasted to all online client!")


@socketio.on_error_default
def default_error_handler(e):
	status = 'Server encounter an error: ' + request.event["message"]
	print(status)
	emit('event_server_status', GenerateJsonstatus(status), broadcast=True)

'''
@socketio.on('join')
def on_join(data):
	username = data['username']
	room = data['room']
	join_room(room)
	send('user_join', '%s joined' % username, room=room)
	
@socketio.on('leave')
def on_leave(data):
	username = data['username']
	room = data['room']
	leave_room(room)
	send('user_leave', '%s left' % username, room=room)
'''

if __name__ == '__main__':
	#app.debug = True
	socketio.run(app, host="0.0.0.0", port=8000)
