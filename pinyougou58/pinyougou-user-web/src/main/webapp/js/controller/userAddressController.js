var app = new Vue({
    el: '#app',
    data:{
        loginName:'',
        list:[],
        district:' ',
        entity:{alias:''}
    },
    methods: {
        findAll: function () {
            axios.get('/address/findAddressListByUserId.shtml').then(function (response) {
                app.list = response.data
            })
        },
        //获取用户名
        getName: function () {
            axios.get('/login/name.shtml').then(function (response) {
                app.loginName = response.data;
            })
        },
        setDefault: function (addressId) {
            axios.get('/address/setDefault/' + addressId + '.shtml').then(function (response) {
                if (response.data.success) {
                    window.location.reload()
                } else {
                    alert(response.data.message)
                }
            })
        },
        dele: function (addressId) {
            var ids = [addressId];
            axios.post('/address/delete.shtml',ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    window.location.reload()
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        findOne: function (addressId) {
            axios.get('/address/findOne/' + addressId + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            }).finally(
                function address(){
                    var first=$('[data-toggle="distpicker"]').find('select:first-child');   //获取select框
                    var second=$('[data-toggle="distpicker"]').find('#city1');
                    var third=$('[data-toggle="distpicker"]').find('select:last-child');

                    var province = app.entity.provinceId;  //省
                    var city = app.entity.cityId;   //市
                    app.district = app.entity.townId;   //区/县

                    first.find('option[value="'+province+'"]').attr("selected","selected").trigger('change');
                    second.find('option[value="'+city+'"]').attr("selected","selected").trigger('change');
                    //third.find('option[value="'+district+'"]').attr("selected","selected").trigger('change');
                }
            )
        },
        add: function () {
            axios.post('/address/add.shtml', this.entity).then(function (response) {
                if (response.data.success) {
                    window.location.reload()
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            this.$set(app.entity,'townId',app.district);
            axios.post('/address/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.reset()
                    window.location.reload()
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        reset:function () {
            this.entity={alias:''}
            this.district=' '
        },
        save: function () {
            if (this.entity.id != null) {
                this.update();
            } else {
                this.add();
            }
        }
    },
    created:function () {
        this.getName();
        this.findAll();
    }
})