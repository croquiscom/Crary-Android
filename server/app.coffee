crary = require 'crary'

setupRouter = (router) ->
  router.get '/ping', (req, res) ->
    res.sendResult response: req.query.message

  router.post '/ping', (req, res) ->
    res.sendResult response: req.body.message

  router.post '/setData', (req, res) ->
    req.session.data = req.body.data
    res.sendResult {}

  router.get '/getData', (req, res) ->
    res.sendResult data: req.session.data

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
