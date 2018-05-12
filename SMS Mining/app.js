var mongoose = require('mongoose');

mongoose.connect('mongodb://localhost/wmywym');

var data = {};
var defaulter = {};
var good = {};
var tempSchema = new mongoose.Schema({
    userid: String,
    smsid: String,
    date: Date,
    from: String,
    content: String,
    type: String,
    read: String
});
var flag = 0;
var ans = [];

var User = mongoose.model('usersms', tempSchema);

function ExtractRecharge(row) {
    var from = row.from;
    var numRegex = /\s[7-9]\d{9}[^0-9]/;

    var num = (numRegex.exec(row.content) + '').match(/\d+/);

    var getAmountRegex = /(Rs|MRP|RC)\.?\s?\d+\.?\d*/;
    var amount = getAmountRegex.exec(row.content);
    amount = (amount + '').match(/\d+\.?\d*/);

    // console.log(i + ") " + row.userid + '  ' +row.from +'   ' + num + " -> " + amount);



    if(!data.hasOwnProperty(row.userid)) {
        data[row.userid] = {rechargeAmount: 0, vendor: {}, number: {}, credited: 0, debited: 0, emi: {}};
    }

    from = from.replace(/.*-/, '').toUpperCase();


    num === null ? num = "unknown" : 1 ;

    if(!data[row.userid].vendor.hasOwnProperty(from))
        data[row.userid].vendor[from] = 0;

    if(!data[row.userid].number.hasOwnProperty(num))
        data[row.userid].number[num] = 0;


    amount === null ? amount = 0 :
        amount = parseFloat(amount);




    data[row.userid].number[num] += amount;

    data[row.userid].vendor[from] += amount

    data[row.userid].rechargeAmount +=  amount;
}

function debit(){

    User.find({content: new RegExp(/debited/i) }, 'userid from content', function(err, res){

        for(var i = 0; i < res.length; i++) {
            var row = res[i];

            if(/(recharge|emi)/i.test(row.content + ''))
                continue;

            var getAmountRegex = /((Rs|MRP|RC|INR)\.?\s*[\S]+) | ([\d,\.]+\s(Rs|MRP|RC|INR))/;
            var amount = getAmountRegex.exec(row.content);
            amount = (amount + '').replace(new RegExp(','), '').match(/\d+\.?\d*/);
            //amount = (amount + '').match(/\d+\.?\d*/);

            //if(row.userid == '906920199365344')
            //   console.log(i + ' ' + row.userid + ' ' + amount + '   ' + row.content);

            if(!data.hasOwnProperty(row.userid)) {
                data[row.userid] = {rechargeAmount: 0, vendor: {}, number: {}, credited: 0, debited: 0, emi: {}};
            }

            amount === null ? amount = 0 :
                amount = parseFloat(amount);

            data[row.userid].debited += parseFloat(amount);
        }

        //console.log(data);
        emi();
        if(i == res.length - 1)
            flag++;

    });
}

function credit() {
    User.find({content: new RegExp(/a\/?c\s.*(credited|deposited)|(credited|deposited).*a\/?c\s/i) }, 'userid from content', function(err, res){

        var j = 0;
        for(var i = 0; i < res.length; i++) {
            var row = res[i];

            if(/(debited|recharge|bsnl|topup|emi)/i.test(row.content + ''))
                continue;

            var getAmountRegex = /((Rs|MRP|RC|INR)\.?\s*[\S]+) | ([\d,\.]+\s(Rs|MRP|RC|INR))/;
            var amount = getAmountRegex.exec(row.content);
            amount = (amount + '').replace(new RegExp(','), '').match(/\d+\.?\d*/);
            //amount = (amount + '').match(/\d+\.?\d*/);

          /*  if(row.userid == '104173729392639966832')
                console.log(j++ + ' ' + row.from + ' ' + amount + '   ' + row.content);*/

            if(!data.hasOwnProperty(row.userid)) {
                data[row.userid] = {rechargeAmount: 0, vendor: {}, number: {}, credited: 0, debited: 0, emi: {}};
            }

            amount === null ? amount = 0 :
                amount = parseFloat(amount);

            data[row.userid].credited += parseFloat(amount);
        }

        debit();

        // console.log(data);
        /*for (var x in data) {
         var temp = {};
         temp = data[x];
         //  data[x].userid = x;
         temp.userid = x;
         //data[x];
         ans.push(temp);
         //console.log(x);
         }
         console.log(ans.length);
         writeToFile(ans);*/

        //console.log(JSON.stringify(ans));


        //console.log(ans);
    });


}



User.find({content: new RegExp(/recharge.*\ssuccess/i) }, 'userid from content', function(err, res){

   for(var i = 0; i < res.length; i++) {
       // var row = res[i];

        //console.log(i + '   ' + row.content);
       ExtractRecharge(res[i]);


   }

   if(i == res.length - 1)
    flag++;
   // console.log(data);

    credit();

   /// for (var x in data) {
   //     var temp = {};
   //     temp = data[x];
   //     //  data[x].userid = x;
   //     temp.userid = x;
   //     //data[x];
   //     ans.push(temp);
   //     //console.log(x);
   // }
   //
   //
   //
   // writeToFile(ans);
   //
   // console.log(ans.length);


    //console.log(ans);
   // console.log(res);


});

//new RegExp(/a\/?c\s.*(credited|deposited)|(credited|deposited).*a\/?c/)


function processingData() {
    for(x in data) {
        var cnt = 0;

        if(data[x]['credited'] == 0)
            cnt++;
        if(data[x]['debited'] == 0)
            cnt++;
        if(data[x]['rechargeAmount'] == 0)
            cnt++;

        if(cnt > 1)
            delete data[x];
        else {
            delete data[x]['emi'];
            delete data[x]['vendor'];
            delete data[x]['number'];
        }

    }
}
function getGood() {
    User.find({content: new RegExp(/(debit|\sdr\s).*\semi\s/i) }, 'userid from content', function(err, res) {

   //     console.log('length ' + res.length);
        for(var i = 0; i < res.length; i++) {
            var x = res[i];
            if(!defaulter.hasOwnProperty(x.userid)) {
                defaulter[x.userid]++;
            }
        }

        //console.log('good ');
        for (x in defaulter) {
            data[x].defaulter = 'YES';
        }

        //console.log('new count of defaulters ' + defaulter.length);

        processingData();


        for (x in data) {

            if(!data[x].hasOwnProperty('defaulter'))
                data[x].defaulter = 'NO';


            var temp = {};
            temp = data[x];
            //  data[x].userid = x;
            //temp.userid = x;
            //data[x];
            ans.push(temp);
            //console.log(x);
        }
        //console.log(ans);
        //console.log('ans');
        writeToFile('/my.json', ans);

        console.log(ans.length);

    });






}



function emi() {

    User.find({content: new RegExp(/\semi.*due/i) }, 'userid from content', function(err, res){

        var j = 0;
        for(var i = 0; i < res.length; i++) {
            var row = res[i];

            var getAmountRegex = /((Rs|MRP|RC|INR)\.?\s*[\S]+) | ([\d,\.]+\s(Rs|MRP|RC|INR))/;
            var amount = getAmountRegex.exec(row.content);
            amount = (amount + '').replace(new RegExp(','), '').match(/\d+\.?\d*/);
            //amount = (amount + '').match(/\d+\.?\d*/);

            //if(row.userid == '1019008848109270')
             //   console.log(row.userid + ' ' + row.from + ' ' + amount + '   ' + row.content);

            if(!data.hasOwnProperty(row.userid)) {
                data[row.userid] = {rechargeAmount: 0, vendor: {}, number: {}, credited: 0, debited: 0, emi: {}};
            }

            amount === null ? amount = 0 :
                amount = parseFloat(amount);

            if(!data[row.userid].emi.hasOwnProperty(amount)) {
                data[row.userid].emi[amount] = 0;
            }

            data[row.userid].emi[amount]++;
            if(!defaulter.hasOwnProperty(row.userid))
                defaulter[row.userid]++;

        }

        // console.log(data);



        //console.log(JSON.stringify(ans));
        console.log('defaulters ');
        var d = 0;
        //for (x in defaulter) {
        //    console.log(x);
        //    d++;
        //}

        console.log('old length' + defaulter.length);
        getGood();

    });

}


function writeToFile(outputFilename, ans) {
    var fs = require('fs');

    var myData = {
        data: ans,
        version:'1.0'
    }

    //var outputFilename = '/my.json';

    fs.writeFile(outputFilename, JSON.stringify(myData, null, 4), function(err) {
        if(err) {
            console.log(err);
        } else {
            console.log("JSON saved to " + outputFilename);
        }
    });
}

//var mongodb = require('mongodb');
//
//var url = 'mongodb://localhost:27017/wmywym';
//
//var MongoClient = mongodb.MongoClient;
//
//MongoClient.connect(url, function(err, db){
//
//    if(err)
//        console.log('error connecting to database', err);
//    else {
//        console.log('connection established to ', url);
//
//
//
//
//
//
//        db.close();
//    }
//});