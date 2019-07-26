var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        len:1,
        smsCode:'',
        timeStringPay:'',
        timeStringSend:'',
        status:['','未付款','已付款','未发货','已发货','交易成功','交易关闭','待评价'],
        ids:[],
        searchEntity:{userName:''},
        loginName:""
    },
    methods: {
        //获取用户名
        getName: function () {
            axios.get('/login/name.shtml').then(function (response) {
                app.loginName = response.data;
            })
        },

        //获取验证码
        createSmsCode: function () {
            axios.get('/user/sendCode.shtml?phone=' + this.entity.phone).then(function (response) {
                if (response.data.success) {
                    alert(response.data.message)
                } else {
                    alert(response.data.message)
                }
            })
        },


        /*
        * 查询我的订单列表
        * */
        findOrderList: function () {
            axios.post('/myOrderList/search.shtml').then(function (response) {
                //获取数据
                app.list = response.data;

                for (var i = 0; i < app.list.length; i++) {
                    var time = app.list[i].order.createTime;
                    /*下单后24小时内付款*/
                    var date = new Date();
                    date.setTime(time);
                    app.list[i].order.createTime = date.toLocaleString();
                    /*
                    * 倒计时
                    * */
                    if (app.list[i].order.status == 1) {
                        time = 15*24*60*60*1000+time
                        var date1 = new Date();
                        var time1 = time-date1;
                        var clock = window.setInterval(function () {
                            time1=time1-1000
                            app.timeStringPay=app.convertTimeString(time1)
                            if (time1<=0){
                                window.clearInterval(clock);
                            }
                        },1000)
                    }else {//暂不考虑status==3,4,5,6的状态
                        time = 30*24*60*60*1000+time
                        var date2 = new Date();
                        var time2 = time-date2;
                        var clock = window.setInterval(function () {
                            time2=time2-1000
                            app.timeStringSend=app.convertTimeString(time2)

                            if (time2<=0){
                                window.clearInterval(clock);
                            }
                        },1000)
                    }

                }
            });
        },


        //转换时间格式
        convertTimeString:function (time) {
            var allSecond = Math.floor(time/1000);
            //设置年月日
            var days = Math.floor(allSecond/24/3600);
            var hours = Math.floor((allSecond-days*24*3600)/3600);
            var minutes = Math.floor((allSecond-days*24*3600-hours*3600)/60);
            var seconds = Math.floor(allSecond-days*24*3600-hours*3600-minutes*60);

            //设置格式
            if (days<10){
                days = '0'+days;
            }

            if (hours<10){
                hours = '0'+hours;
            }

            if (minutes<10){
                minutes = '0'+minutes;
            }

            if (seconds<10){
                seconds = '0'+seconds;
            }
            return days+'天'+hours+'时'+minutes+'分'+seconds+'秒';
        },
        //该方法只要不在生命周期的
        add: function () {
            axios.post('/user/add/' + this.smsCode + '.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    window.location.href = "home-index.html";
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/user/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save: function () {
            if (this.entity.id != null) {
                this.update();
            } else {
                this.add();
            }
        },
        findOne: function (id) {
            axios.get('/user/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/user/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        }


    },
    //钩子函数 初始化了事件和
    created: function () {
        this.getName();
        this.findOrderList();
    }

})
