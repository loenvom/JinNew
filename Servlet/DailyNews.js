var express = require('express');
var app = express();
var fs = require('fs')
var formidable = require("formidable");
const { rejects } = require('assert');
const { resolve, format } = require('path');
var db = require('./mysql_util')
var AipContentCensorClient = require("baidu-aip-sdk").contentCensor;

// 设置APPID/AK/SK
var APP_ID = "20591820";
var API_KEY = "H5C0ePVcwUNrXufo1gZGiXis";
var SECRET_KEY = "W7nIQfVUuQZT5l7fHDxvspmGwO86bFPq";

// 新建一个对象，建议只保存一个对象调用服务接口
var client = new AipContentCensorClient(APP_ID, API_KEY, SECRET_KEY);



app.all('*', function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "X-Requested-With");
    res.header("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS");
    res.header("X-Powered-By",' 3.2.1')
    res.header("Content-Type", "application/json;charset=utf-8");
    next()
});

app.post('/register', function(req, res){
    
    //创建套件用于解析 Multidata-form 数据表
    var form = new formidable.IncomingForm();
    //设置文件上传存放地址，在这里配置了本地文件的默认保存位置，
    //此时文件名称是一个upload开头的随机字符串
    form.uploadDir = "./uploads";
    //执行里面的回调函数的时候，表单已经全部接收完毕了。
    form.parse(req, function(err, fields, files) {
        
        for(key in files)
        {
            /**
                遍历整个文件对象，将upload——XXXX格式的文件名称修改为自己需要的名称
                在这里由于我在前端中传回了一个名称，所有就修改为这个名称
            **/
            let file = files[key]
            fs.rename('.//'+file['path'], './/uploads//' + file['name'], err=>{
                if(err) console.log(err)
            })
        }

        /**
            { nickname: '1', userid: '2', password: '3' }
            fields 数据区域的储存格式
        **/

        // 获取前端向后端传递的参数

        let nickname = fields['nickname']
        let userid = fields['userid']
        let password = fields['password']

        db.selectdata('user', ' where userid = "' + userid + '" ', result=>{
            if(result.length > 0) 
            {
                // 数据库中查询结果大于 0 说明该 userid
                // 已经存在，返回结果 ‘EXIST_USERID’
                res.end('EXIST_USERID')
            }else
            {
                // 将内容插入数据库
                let params = {
                    'nickname' : nickname, 
                    'userid' : userid,
                    'password' : password
                }
                db.insertdata('user', params, result=>{
                    res.end('REGISTER_SUCCESS')
                }, err=>{

                })
            }
        }, err=>{
            
        })
    });

    
    
});

app.get('/login', (req, res)=>{
    // 获取数据
    let userid = req['query']['userid']
    let password = req['query']['password']
    db.selectdata('user', ' where userid = "' + userid + '" ', result=>{
        if(result.length == 0)
        {
            // 查询结果为空，说明该账户不存在
            res.end('USERID_NOT_EXIST')
        }else{
            // 获取结果
            result = result[0]
            // 将密码与数据库中的密码进行检验
            if(result['password'] == password)
            {
                // 密码一致，通过验证，登录成功
                res.end(result['nickname'])
            }else{
                // 登录失败
                res.end('WRONG_PASSWORD')
            }
        }
    }, err=>{

    })
})

app.get('/', (req, res)=>{
    res.end("!!!!");
})

app.get('/comment', (req, res)=>{
    let uniquekey = req['query']['uniquekey']
    console.log(uniquekey)
    db.selectdata('comment', ' where uniquekey = "' + uniquekey + '" ', comments=>{

        let total = comments.length
        for(key in comments)
        {
        
            
            (function(key){
                comment = comments[key]
                db.selectdata('user', ' where userid = "' + comment['userid'] + '" ', result=>{
                    comments[key]['nickname'] = result[0]['nickname']
                 
                    total--;
    
                    if(total == 0)
                    {
                        res.end(JSON.stringify(comments))
                        console.log(JSON.stringify(comments))
                    }
                }, err=>{
                
                })   
            })(key) 
        }
       
        
            
        

    }, error=>{

    })
})

app.get('/send', (req, res)=>{
    let userid = req['query']['userid']
    let words = req['query']['words']
    let uniquekey = req['query']['uniquekey']
    let time = req['query']['time']
    params = {
        'userid' : userid,
        'words' : words,
        'uniquekey' : uniquekey,
        'time' : time
    }
    client.textCensorUserDefined(words).then(function(data) {
        /*
        {"conclusion":"不合规","log_id":16096720353914868,"data":[{"msg":"存在低俗辱骂不合规","conclusion":"不合规",
        "hits":[{"probability":0.76831967,
        "datasetName":"百度默认文本反作弊库","words":[]}],"subType":5,"conclusionType":2,"type":12}],"conclusionType":2}
        */
        console.log(data)
        if(data['conclusion'] == '合规')
        {
            db.insertdata('comment', params, result=>{
                res.end('polite')
            }, error=>{
        
            })
        }else{
            res.end("impolite");
        }
    }, function(e) {
        console.log(e)
    });
  
})

app.get('/collect', (req, res)=>{
    // 获取数据
    console.log('collect');
    let userid = req['query']['userid']
    let uniquekey = req['query']['uniquekey']
    let title = req['query']['title']
    let image_src = req['query']['image_src']
    let content_url = req['query']['content_url']
    let params = {
        'userid' : userid,
        'uniquekey' : uniquekey,
        'title' : title,
        'image_src' : image_src,
        'content_url' : content_url
    }
    db.selectdata('collect', ' where userid = "' + userid + '" and uniquekey = "' + uniquekey + '" ', result=>{
            if(result.length > 0)
            {
                // 查询结果不为空，说明收藏记录已经存在
                res.end('collect_exist')
            }else
            {
                // 写入数据库之后，向前端返回操作结果
                // 在这一过程中，需要注意操作之间的异步顺序
                db.insertdata('collect', params, result=>{
                    res.end('true')
                }, error=>{
                    console.log('Mysql Collect insert fail', error)
                })
            }
    }, error=>{
        console.log('Mysql collect select error')
    })
})

app.get('/get_collect', (req, res)=>{
    let userid = req['query']['userid']
    let pack = {
        'reason' : 'success', 
        'result' : {
            'stat' : '1',
            'data' : []
        }
    }
    console.log(userid)
    db.selectdata('collect', ' where userid = "' + userid + '" ', result=>{
        
        var total = result.length

        for(index in result)
        {
            (function(index){
                result[index]['url'] = result[index]['content_url']
                result[index]['thumbnail_pic_s'] = result[index]['image_src']
                total--;

                console.log(result)
            })(index)
        }
        while(true)
        {
            if(total == 0)
            {
                pack['result']['data'] = result
                res.end(JSON.stringify(pack))
                break;
            }
        }
        console.log(JSON.stringify(pack))
    }, error=>{
        res.end("false")
    })
})

/**
    访问文件的返回请求
**/

app.get('/uploads/*', function(req, res){
    
    //设置请求的返回头type,content的type类型列表见上面
    res.setHeader("Content-Type", 'image/jpeg');
    //格式必须为 binary 否则会出错
    var content = fs.readFileSync(__dirname + "/" + req.url,"binary"); 
    res.writeHead(200, "Ok");
    res.write(content,"binary"); //格式必须为 binary，否则会出错
    res.end();
    
})

// 处理删除收藏的功能

app.get('/remove_collect', function(req, res){
    
    let userid = req['query']['userid']
    let uniquekey = req['query']['uniquekey']

    db.deleltedata('collect', ' where uniquekey="' + uniquekey + '" and userid="'+userid+'" ', result=>{

        res.end('true')
    }, error=>{
        res.end('false')
    })
})

app.get('/remove_comment', function(req, res){
    
    let userid = req['query']['userid']
    let uniquekey = req['query']['uniquekey']
    let words = req['query']['words']
    let time = req['query']['time']

    console.log(userid, uniquekey, words, time)
    db.deleltedata('comment', ' where uniquekey="' + uniquekey + '" and userid="'+userid+'" and words="'
    +words+'" and time="'+time+'" ', result=>{

        res.end('true')
    }, error=>{
        res.end('false')
    })
})
app.listen(8080);