var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : 'pumpkin',
  database : 'dailynews'
});
connection.connect();
/**
    异步回调请求
    封装插入语句
    params：
        表名
        插入数据的 JSON 对象
        resolve 回调事件
        reject 回调事件
**/

/** 
    调用测试
    params = {
        'nickname' : 'loenvom', 
        'eid' : '919087313',
        'pass' : 'zzzzzz'
    }

    insertdata('User', params, (rows)=>{
        console.log(rows)
    }, (err)=>{
        console.log(err)
    })
**/



/**
 * 
 * 调用测试
 *  selectdata('User', ' where nickname = "zzz" ', (rows)=>{
        console.log(rows.length)
    }, (err)=>{
        console.log(err);
    }) 
 */

module.exports = {
    selectdata : function selectdata(table_name, limitation, resolve, reject)
    {
        let sql = 'select * from ' + table_name + ' ' + limitation

        connection.query(sql, (err, rows) => {
            if(err) reject(err)
            else resolve(rows)
        })
    },
    insertdata: function insertdata(table_name, params, resolve, reject)
    {    
        var sql = "insert into " + table_name + " ("
        let first = 0
        for(key in params)
        {
            if(first == 0)
            {
                sql = sql + key
                first = 1
            }else{
                sql = sql + ',' + key
            }
        }
        sql = sql + ') values ('
        first = 0
        for(key in params)
        {
            if(first == 0)
            {
                sql = sql + '"' + params[key] + '"'
                first = 1
            }else{
                sql = sql + ', "' + params[key] + '"'
            }
        }
        sql = sql + ')'
        console.log(sql)
        connection.query(sql, (err, rows) => {
            if(err) reject(err)
            else resolve(rows)
        })
    },
    updatedata : function updatedata(table_name, limitation, params, resolve, reject){
        sql = 'update ' + table_name + ' set ' + params[0] + ' = ' + params[1] + limitation
        connection.query(sql, (err, rows)=>{
            if(err) reject(err)
            else resolve(rows)
        })
    },
    deleltedata : function deleltedata(table_name, limitation, resolve, reject)
    {
        sql = 'delete from ' + table_name + limitation

        console.log(sql)
        connection.query(sql, (err, rows)=>{
            if(err) reject
            else resolve(rows)
        })
    }
}




