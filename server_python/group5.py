#IEMS5722 Mobile Network Programming and Distributed Server Architecture
#assignment 4

#student id:   1094635483
#student name: Liu Chun Kit

#!/usr/bin/env python
#references:
#http://www.anthonydebarros.com/2012/03/11/generate-json-from-sql-using-python/
#http://stackoverflow.com/questions/28910217/jsonify-a-list-of-custom-objects

from flask import Flask
from flask import jsonify
from flask import request
from flask import make_response
from flask import send_file
from pytz import timezone
import MySQLdb as mysql
import collections
import json
import traceback
import pytz
from task import distribute
import datetime

#individual chatroom class for get_chatrooms
class chatroom:
    def __init__(self, id, name):
        self.id = id
        self.name = name
		
    def serialize(self):
        return {
        'id': self.id,
        'name': self.name
        }

#chatroom list class for get_chatrooms
class json_chatrooms:
    def __init__(self, status, chatroomlist):
        self.status = status
        self.chatroomlist = chatroomlist

    def serialize(self):
        return {
        'data': [r.serialize() for r in self.chatroomlist],
        'status': self.status
        }

#message class for get_messages
class message:
    def __init__(self, message, username, userid, msgtime, imagename):
        self.message = message
        self.username = username
        self.userid = userid
        self.msgtime = msgtime
        self.imagename = imagename

    def serialize(self):
        return {
        'message': self.message,
        'name': self.username,
        'timestamp': self.msgtime.strftime('%Y-%m-%d %H:%M:%S'),
        'user_id': self.userid,
        'image': self.imagename
        }

#message page class containing message in a particular page
# class messagepage:
    # def __init__(self, currentpage, messagelist, totalpages):
        # self.currentpage = currentpage
        # self.messagelist = messagelist
        # self.totalpages = totalpages

    # def serialize(self):
        # return {
        # 'current_page': self.currentpage,
        # 'messages': [m.serialize() for m in self.messagelist],
        # 'total_pages': self.totalpages
        # }

#message class for get_messages
class json_messages:
    def __init__(self, status, currentpage, messagelist, totalpages):
        self.status = status
        self.currentpage = currentpage
        self.messagelist = messagelist
        self.totalpages = totalpages
	
    def serialize(self):
        return {
        'data': [m.serialize() for m in self.messagelist],
        'page': self.currentpage,
        'total_pages': self.totalpages,
        'status': self.status
        }
		
#generic error class
class error:
    def __init__(self, status, message):
        self.status = status
        self.message = message

    def serialize(self):
        return {
        'message': self.message,
        'status': self.status
        }
		
#generic success class
class success:
    def serialize(self):
        return {
        'status': 'OK'
        }
		
app = Flask(__name__)

#constants for connecting MySQL
DB_HOST = 'localhost'
DB_USER = 'iemsuser'
DB_PASSWORD = 'iems5722'
DB_SCHEMA = 'iems5722'

@app.route('/api/group5/get_chatrooms', methods=['GET'])
def get_chatrooms():
    try:
        conn = mysql.connect(DB_HOST, DB_USER, DB_PASSWORD, DB_SCHEMA, charset='utf8')
        cursor = conn.cursor(mysql.cursors.DictCursor)
        cursor.execute("select id, name from chatrooms order by id")
        rows = cursor.fetchall()
        cursor.close()
        conn.close()

        roomlist = []
        for room in rows:
            r = chatroom(room["id"], room["name"])
            roomlist.append(r)
        result = json_chatrooms('OK', roomlist)
	
        return jsonify(result.serialize())
    except Exception as e:
        err = error('ERROR', str(e))
        return jsonify(err.serialize())


@app.route('/api/group5/send_message', methods=['POST'])
def send_message():
    try:
        #get query params
        in_chatroom_id = request.values.get('chatroom_id')
        in_user_id = request.values.get('user_id')
        in_user_name = request.values.get('name')
        in_message = request.values.get('message')
		
        print('chatroom_id={}'.format(in_chatroom_id))
        print('user_id={}'.format(in_user_id))
        print('user_name={}'.format(in_user_name))
        #print('message={}'.format(in_message))

        conn = mysql.connect(DB_HOST, DB_USER, DB_PASSWORD, DB_SCHEMA, charset='utf8')
        cursor = conn.cursor(mysql.cursors.DictCursor)
		
		#insert msg to db
        add_message = "insert into messages (chatroom_id, user_id, name, message, timestamp) values (%s, %s, %s, %s, NOW())";
        data = (in_chatroom_id, in_user_id, in_user_name, in_message)
        cursor.execute(add_message, data)
        conn.commit()

        chatroom_name = get_chatroom_name(cursor, in_chatroom_id)
		
		#get all tokens to broadcast msg
        try:
            local_tz = timezone('Asia/Hong_Kong')
            m = {}

            m['message'] = in_message
            m['name'] = in_user_name
            m['timestamp'] = pytz.utc.localize(datetime.datetime.now()).astimezone(local_tz).strftime('%Y-%m-%d %H:%M:%S')
            m['user_id'] = in_user_id
            m['chatroom_name'] = chatroom_name

            #m['chatroom_id'] = in_chatroom_id
            #m['image'] = file_name
            print(m)
            data_message = ("select token from push_tokens")# where user_id=%s")
            param_message = (in_user_id)
            cursor.execute(data_message)#, param_message)
            rows = cursor.fetchall()
            mylist = []
            for token_item in rows:
                mylist.append(token_item["token"])
                #distribute.delay(token_item["token"], chatroom_name, jsonify(m.serialize()).get_data(as_text=True))
            distribute.delay(mylist, 'a', m)
            print(mylist)
        except Exception as ee:
            print('fcm send fail:' + str(ee))
            err = error('ERROR', str(ee))
            return jsonify(err.serialize())
        finally:
            cursor.close()
            conn.close()

        print('success')
        return jsonify(success().serialize())
    except Exception as e:
        print('fail:' + str(e))
        traceback.print_exc()
        err = error('ERROR', str(e))
        return jsonify(err.serialize())

@app.route('/api/group5/get_messages/', methods=['GET'])
def get_messages():
    try:
        PAGE_SIZE = 20
		#get query params
        chatroom_id = request.args.get('chatroom_id')
        page = request.args.get('page')
		
        data_message = ("select user_id, name, message, timestamp, image_name from messages " 
            " where chatroom_id=%s order by id desc limit %s, %s")
        param_message = (chatroom_id, PAGE_SIZE * (int(page) - 1), PAGE_SIZE)
        conn = mysql.connect(DB_HOST, DB_USER, DB_PASSWORD, DB_SCHEMA, charset='utf8')
        cursor = conn.cursor(mysql.cursors.DictCursor)
        cursor.execute(data_message, param_message)
        rows = cursor.fetchall()

        #get message count to calculate total pages		
        cursor.execute("select 1 from messages where chatroom_id={}".format(chatroom_id))
        msg_count = cursor.rowcount
        cursor.close()
        conn.close()

		#assume run in HK (UTC+8), convert timezone
        messagelist = []
        local_tz = timezone('Asia/Hong_Kong')
        for msg in rows:
            t = pytz.utc.localize(msg["timestamp"])
            local_time = t.astimezone(local_tz)
            m = message(msg["message"], msg["name"], msg["user_id"], local_time, msg["image_name"])
            messagelist.append(m)
			
        pages = msg_count / PAGE_SIZE
        if msg_count % PAGE_SIZE > 0:
            pages += 1

        result = json_messages('OK', int(page), messagelist, pages)
	
        return jsonify(result.serialize())
		
    except Exception as e:
        err = error('ERROR', str(e))
        traceback.print_exc()
        return jsonify(err.serialize())

@app.route('/api/group5/submit_push_token', methods=['POST'])
def submit_push_token():
    try:
        #get query params
        user_id = request.values.get('user_id')
        token = request.values.get('token')

        conn = mysql.connect(DB_HOST, DB_USER, DB_PASSWORD, DB_SCHEMA)
        cursor = conn.cursor(mysql.cursors.DictCursor)

        cursor.execute("select user_id, token from push_tokens where user_id={}".format(user_id))
        token_count = cursor.rowcount
		#has record, update new token
        if token_count == 1:
            upd_message = "update push_tokens set token=%s where user_id=%s";
            data = (token, user_id)
            cursor.execute(upd_message, data)
            conn.commit()
            print('update token for ' + user_id)
        else:
            add_message = "insert into push_tokens (user_id, token) values (%s, %s)";
            data = (user_id, token)
            cursor.execute(add_message, data)
            conn.commit()
            print('insert token for ' + user_id)
        cursor.close()
        conn.close()

        print('success')
        return jsonify(success().serialize())
    except Exception as e:
        err = error('ERROR', str(e))
        traceback.print_exc()
        return jsonify(err.serialize())

@app.route('/api/group5/upload_image', methods=['POST'])
def upload_image():
    print request.headers
    print('upload image')
    try:
        in_chatroom_id = request.headers.get('chatroomid')
        in_user_id = request.headers.get('userid')
        in_user_name = request.headers.get('name')
        in_timestamp = request.headers.get('timestamp')
		
        err_msg = ''
        if in_chatroom_id is None:
            print('NULL in_chatroom_id')
            err_msg += 'null in_chatroom_id;'
        if in_user_id is None:
            print('NULL USER ID')
            err_msg += 'null user id;'
        if in_user_name is None:
            print('NULL in_user_name')
            err_msg += 'null in_user_name;'
        if in_timestamp is None:
            print('NULL in_timestamp')
            err_msg += 'null in_timestamp;'
        if err_msg != '':
            err = error('ERROR', err_msg)
            return jsonify(err.serialize())

        print('user id=' + in_user_id)
        print('time=' + in_timestamp)
        file_name = in_user_id + '_' + in_timestamp+ '.jpg'
        print('file is ' + file_name)
		
        request.get_data()
        f = open('/home/hmcheng/Desktop/hqdefault.jpg', 'w')
        f.write(request.data)
        f.close()

        conn = mysql.connect(DB_HOST, DB_USER, DB_PASSWORD, DB_SCHEMA, charset='utf8')
        cursor = conn.cursor(mysql.cursors.DictCursor)
		
		#insert msg to db
        add_message = "insert into messages (chatroom_id, user_id, name, message, timestamp, image_name) values (%s, %s, %s, %s, NOW(), %s)";
        data = (in_chatroom_id, in_user_id, in_user_name, '', file_name)
        cursor.execute(add_message, data)
        conn.commit()
		
        chatroom_name = get_chatroom_name(cursor, in_chatroom_id)

		#get all tokens to broadcast msg
        try:
            local_tz = timezone('Asia/Hong_Kong')
            m = {}

            m['message'] = ''
            m['name'] = in_user_name
            m['timestamp'] = pytz.utc.localize(datetime.datetime.now()).astimezone(local_tz).strftime('%Y-%m-%d %H:%M:%S')
            m['user_id'] = in_user_id
            m['chatroom_name'] = chatroom_name
            m['chatroom_id'] = in_chatroom_id
            m['image'] = file_name

            print(m)
            data_message = ("select token from push_tokens")# where user_id=%s")
            param_message = (in_user_id)
            cursor.execute(data_message)#, param_message)
            rows = cursor.fetchall()
            mylist = []
            for token_item in rows:
                mylist.append(token_item["token"])
            distribute.delay(mylist, 'a', m)
            print(mylist)
        except Exception as ee:
            print('fcm send fail:' + str(ee))
            err = error('ERROR', str(ee))
            return jsonify(err.serialize())
        finally:
            cursor.close()
            conn.close()

        print('success')
        return jsonify(success().serialize())
    except Exception as e:
        err = error('ERROR', str(e))
        traceback.print_exc()
        return jsonify(err.serialize())
		
def get_chatroom_name(cursor, in_chatroom_id):
    sel_message = ("select * from chatrooms where id=%s")
    param_sel = (in_chatroom_id)
    cursor.execute(sel_message, param_sel)
    chatroom = cursor.fetchone()
    chatroom_name = chatroom["name"]
    print('chatroom={}'.format(chatroom_name))
    return chatroom_name

@app.route('/api/group5/download_image', methods=['GET'])
def download_image():
    try:
        #get query params
        file_name = request.values.get('file_name')
        return send_file('images/' + file_name, mimetype='image/jpg')
    except Exception as e:
        err = error('ERROR', str(e))
        traceback.print_exc()
        return jsonify(err.serialize())

if __name__ == "__main__":
    app.run(host='0.0.0.0')