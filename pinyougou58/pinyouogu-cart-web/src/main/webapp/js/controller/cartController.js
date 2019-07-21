var app = new Vue({
    el: "#app",
    data: {
        order:{paymentType:''},
        address:{},
        addressList:[],
        cartList:[],
        totalMoney:0,//总金额
        totalNum:0//总数量
    },

    methods: {
        //添加order
        submitOrder: function () {
            this.$set(this.order,'receiver',this.address.contact);
            this.$set(this.order,'receiverMobile',this.address.mobile);
            this.$set(this.order,'receiverAreaName',this.address.address)
            axios.post('/order/add.shtml', this.order).then(function (response){
                if (response.data.success) {
                    window.location.href="pay.html"
                }else {
                    alert(response.data.message)
                }
            })
        },

        //选择支付类型
        selectType:function(type){
          this.$set(this.order,'paymentType',type);
        },

        /***
         * 选中地址
         * @param address
         */
        selectAddress:function(address){
            this.address=this.addressList[0];
            this.address=address
        },

        /***
         * 地址是否选中
         * @param address
         * @returns {boolean}
         */
        isSelectedAddress:function(address){
            if(this.address==address){
                return true;
            }else{
                return false;
            }
        },

        /***
         * 查找address
         */
        findAddressList:function(){
            axios.get('/address/findAddressListByUserId.shtml').then(function(response){
                    app.addressList=response.data;
                    for(var i=0;i<app.addressList.length;i++){
                        if(app.addressList[i].isDefault==1){
                            app.address=app.addressList[i];
                        }
                    }
            })
        },

        /**
         * 添加商品
         * @param tbItemId
         * @param num
         */
        addGoodsToCartList:function(itemId,num){
            axios.get('/cart/addGoodsToCartList.shtml',{
                params:{
                    itemId:itemId,
                    num:num
                }
              }
            ).then(function(response){
                if(response.data.success){
                    app.findCartList()
                }else{
                    alert(response.data.message)
                }
            })
        },

        /**
         * 查看购物车
         */
        findCartList: function () {
            this.totalMoney=0;
            this.totalNum=0;
            axios.get('/cart/findCartList.shtml').then(function(response){
                app.cartList=response.data;

                //获取totalNum totalMoney
                var carts=response.data;
                for(var i=0; i<carts.length;i++){
                    var cart = carts[i];
                    for(var j=0; j<cart.orderItemList.length;j++){
                        app.totalMoney+=cart.orderItemList[j].totalFee;
                        app.totalNum+=cart.orderItemList[j].num;
                    }
                }
            })
        }
    },
    created: function () {
        this.findCartList();
        if(window.location.href.indexOf("getOrderInfo.html")!=-1){
            this.findAddressList();
        }
    }
})