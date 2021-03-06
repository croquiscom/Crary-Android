crary = require 'crary'
fs = require 'fs'
multipart = require 'connect-multiparty'

setupRouter = (router) ->
  router.get '/ping', (req, res) ->
    res.sendResult response: req.query.message

  router.post '/ping', (req, res) ->
    res.sendResult response: req.body.message

  router.get '/echo', (req, res) ->
    res.sendResult req.query

  router.post '/echo', multipart(), (req, res) ->
    if req.files
      for field, file of req.files
        req.body[field] = file_name: file.name, size: file.size, type: file.type
        fs.unlink file.path
    res.sendResult req.body

  router.post '/setData', (req, res) ->
    req.session.data = req.body.data
    res.sendResult {}

  router.get '/getData', (req, res) ->
    res.sendResult data: req.session.data

  router.get '/error', (req, res) ->
    res.type 'application/json; charset=utf-8'
    .status req.query.status or 400
    .json error: req.query.error, description: req.query.description

  router.get '/plain', (req, res) ->
    res.status req.query.status or 200
    .send 'Plain Text Result'

  router.post '/binary', (req, res) ->
    buffers = []
    req.on 'data', (chunk) ->
      buffers.push chunk
    req.on 'end', ->
      buffer = Buffer.concat buffers
      for i in [0...buffer.length]
        buffer.writeInt8 buffer.readInt8(i)+i, i
      res.send buffer

app = crary.express.createApp
  project_root: __dirname
  log4js_config:
    appenders: [ {
      type: 'console'
    } ]
    replaceConsole: false
  redis_port: 6379
  session_ttl: 300
  session_secret: 'crary'
  routers:
    '': setupRouter
require('http').createServer(app).listen 3000
