var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{province:'广东省',city:'广州市',district:'东城区',job:'请选择'},
        jobs:['程序员','产品经理','UI','总经理'],
        birthYear:'',
        birthMonth:'',
        birthDay:'',
        province:'',
        city:'',
        url:'',
        district:'',
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
        //方法 是用于图片上传使用 点击上传的按钮的时候调用
        uploadFile:function () {
            //创建一个表单的对象
            var formData=new FormData();

            //添加字段    formData.append('file'           ==> <input type="file"  name="file" value="文件本身">
            //            file.files[0]    第一个file 指定的时候 标签中的id   后面的files[0] 表示获取 选中的第一张文件 对象。File
            formData.append('file', file.files[0]);

            axios({
                url: 'http://localhost:9110/upload/uploadFile.shtml',
                //数据  表单数据
                data: formData,
                method: 'post',
                //设置表单提交的数据类型
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                //开启跨域请求携带相关认证信息
                withCredentials:true
            }).then(function (response) {
                //文件上传成功
                if(response.data.success){
                    app.entity.headPic=response.data.message;
                    alert("上传成功")
                }else{
                    //上传失败
                    alert(response.data.message);
                }
            })
        },

        fillIn:function (job) {
            this.entity.job=job;
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
            var day = this.birthYear+'-'+this.birthMonth+'-'+this.birthDay
            var dateInput = new Date(day);
            this.$set(app.entity,'birthday',dateInput);
            this.$set(app.entity,'district',app.district);

            axios.post('/user/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    alert("更新成功")
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
        //查看用户信息数据回显
        findOne: function () {
            axios.get('/user/findOne.shtml').then(function (response) {
                app.entity = response.data;
                var date = new Date();
                date.setTime(response.data.birthday);
                app.birthYear = date.getFullYear();
                app.birthMonth = date.getMonth()+1;
                app.birthDay = date.getDate();

                // this.$set(app.entity,'province',response.data.province);
                // this.$set(app.entity,'city',response.data.city);
                // this.$set(app.entity,'province',response.data.province);
                /*$().distpicker('reset', true);
                $('[data-toggle="distpicker"]').distpicker({
                    province: app.entity.province,
                });*/
                /*$.fn.distpicker.setDefaults = function (options) {
                    $.extend({
                        province: '云南省'
                    }, options);
                };*/




            }).catch(function (error) {
                console.log("1231312131321");
            }).finally(
                function address(){
                    var first=$('[data-toggle="distpicker"]').find('select:first-child');   //获取select框
                    var second=$('[data-toggle="distpicker"]').find('#city1');
                    var third=$('[data-toggle="distpicker"]').find('select:last-child');

                    var province = app.entity.province;  //省
                     var city = app.entity.city;   //市
                    app.district = app.entity.district;   //区/县

                    first.find('option[value="'+province+'"]').attr("selected","selected").trigger('change');
                    second.find('option[value="'+city+'"]').attr("selected","selected").trigger('change');
                    //third.find('option[value="'+district+'"]').attr("selected","selected").trigger('change');
                }

            );
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
        url = window.location.href
        if (url.indexOf("home-index") != -1) {
            this.findOrderList();
        }
        if (url.indexOf("info") != -1) {
            this.findOne();
        }
    },

})




