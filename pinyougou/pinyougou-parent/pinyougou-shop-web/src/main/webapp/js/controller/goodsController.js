var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {tbGoods: {}, tbGoodsDesc: {itemImages: [], customAttributeItems: [],specificationItems:[],itemList:[]}, itemList: []},
        ids: [],
        searchEntity: {},
        image_entity: {url: '', color: ''},
        itemCat1List: [],//一级分类的列表 变量
        itemCat2List: [],//二级分类的列表 变量
        itemCat3List: [],//三级分类的列表 变量
        brandTextList: [],//品牌列表
        specList: [],//规格的数据列表 格式[{id:1,text:'网络'}]
    },
    methods: {
        isChecked:function (specName,specValue) {
            var obj = this.searchObjectByKey(this.entity.tbGoodsDesc.specificationItems,specName,'attributeName');
            console.log(obj);
            if(obj!=null){
                if(obj.attributeValue.indexOf(specValue)!=-1){
                    return true;
                }
            }
            return false;
        },
        //当点击复选框的时候调用 并影响变量：entity.goodsDesc.specficationItems的值
        updateChecked:function($event,specName,specValue){
            let searchObject  = this.searchObjectByKey(this.entity.tbGoodsDesc.specificationItems,specName,'attributeName');
            if (searchObject != null) {
                //searchObject===={"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
                if ($event.target.checked) {
                    searchObject.attributeValue.push(specValue);
                }else{
                    searchObject.attributeValue.splice(searchObject.attributeValue.indexOf(specValue),1);
                    if (searchObject.attributeValue.length == 0) {
                        this.entity.tbGoodsDesc.specificationItems.splice(this.entity.tbGoodsDesc.specificationItems.indexOf(searchObject),1);

                    }
                }
            }else{
                //[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}]
                this.entity.tbGoodsDesc.specificationItems.push({
                    'attributeName':specName,
                    'attributeValue':[specValue],
                })
            }
        },
        /**
         *
         * @param list 从该数组中查询[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}]
         * @param specName  指定查询的属性的具体值 比如 网络
         * @param key  指定从哪一个属性名查找  比如：attributeName
         * @returns {*}
         */
        searchObjectByKey:function (list, specName, key) {
            for (var  i = 0; i < list.length; i++) {
                let specificationItem = list[i];//{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
                if (specificationItem[key] == specName) {
                    return specificationItem;
                }
            }
            return null;
        },
        //点击复选框的时候，调用生成sku列表的变量
        createList: function () {
            //1.定义初始化的值
            this.entity.itemList = [{'spec': {}, 'price': 0, 'num': 0, 'status': '0', 'isDefault': '0'}];


            //2.循环遍历
            var specificationItems = this.entity.tbGoodsDesc.specificationItems;


            for (let i = 0; i < specificationItems.length; i++) {
                //3.获取 规格的名称和规格选项的值 拼接返回一个最新的sku的列表
                var obj = specificationItems[i];
                this.entity.itemList = this.addColumn(
                    this.entity.itemList,
                    obj.attributeName,
                    obj.attributeValue,
                );
            }

        },

        /**
         * 获取 规格的名称 和规格选项的值 拼接 返回一个最新的SKU的列表 方法
         * @param itemList list
         * @param columnName 网络
         * @param columnValue [移动3G,移动4G]
         * @returns {Array}
         */
        addColumn: function (list, columnName, columnValue) {
            var newList=[];

            for (let i = 0; i < list.length; i++) {
                var oldRow = list[i];//
                for (let j = 0; j < columnValue.length; j++) {
                    var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                    var value = columnValue[j];//移动3g
                    newRow.spec[columnName]=value;
                    newList.push(newRow);
                }
            }
            return newList;

        },
        findItemCat1List: function () {
            axios.get('/itemCat/findByParentId/0.shtml').then(function (response) {
                app.itemCat1List = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            })
        },
        tirggerFile: function (event) {
            var file = event.target.files; // (利用console.log输出看file文件对象)
            console.log(file)
            // do something...
        },
        removeImage: function (index) {
            //移除图片
            this.entity.tbGoodsDesc.itemImages.splice(index);
        },
        addImageEntity: function () {
            //添加图片
            this.entity.tbGoodsDesc.itemImages.push(this.image_entity);
        },
        upload: function () {
            var formData = new FormData();
            //参数formData.append('file' 中的file 为表单的参数名  必须和 后台的file一致
            //file.files[0]  中的file 指定的时候页面中的input="file"的id的值 files 指定的是选中的图片所在的文件对象数组，这里只有一个就选中[0]
            formData.append('file', file.files[0]);
            axios({
                url: 'http://localhost:9110/upload/uploadFile.shtml',
                data: formData,
                method: 'post',
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                //开启跨域请求携带相关认证信息
                withCredentials: true
            }).then(function (response) {
                if (response.data.success) {
                    //上传成功
                    console.log(this);
                    app.image_entity.url = response.data.message;
                    console.log(JSON.stringify(app.image_entity));
                } else {
                    //上传失败
                    alert(response.data.message);
                }
            })
        },
        searchList: function (curPage) {
            axios.post('/goods/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;

                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/goods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/goods/findPage.shtml', {
                params: {
                    pageNo: this.pageNo
                }
            }).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data.list;
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add: function () {
            //获取副文本编辑器中的内容传给对象
            this.entity.tbGoodsDesc.introduction = editor.html();
            axios.post('/goods/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    // app.searchList(1);
                    app.entity = {tbGoods: {}, tbGoodsDesc: {}, itemList: []};

                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/goods/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    window.location.href="goods.html";
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save: function () {
            if (this.entity.tbGoods.id != null) {
                this.update();
            } else {
                this.add();
            }
        },
        goListPage:function(){
            window.location.href="goods.html";
        },
        findOne: function (id) {
            axios.get('/goods/findOne/' + id + '.shtml').then(function (response) {

                app.entity = response.data;

                //赋值到富文本编辑器
                editor.html(app.entity.tbGoodsDesc.introduction);
                //转换JSON显示
                app.entity.tbGoodsDesc.itemImages=JSON.parse(app.entity.tbGoodsDesc.itemImages);
                app.entity.tbGoodsDesc.customAttributeItems=JSON.parse(app.entity.tbGoodsDesc.customAttributeItems);
                app.entity.tbGoodsDesc.specificationItems=JSON.parse(app.entity.tbGoodsDesc.specificationItems);

                for (let i = 0; i < app.entity.itemList.length; i++) {
                    let item = app.entity.itemList[i];
                    item.spec=JSON.parse(item.spec);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/goods/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        clearPic:function(){
            app.image_entity= {url: '', color: ''};
        }


    },
    watch: {
        //监控数据的变化，如果最后还剩下一个就直接删除
        'entity.itemList':function (newvalue, oldvalue) {
            //如果是相同的数据那么直接赋值为空即可
            console.log(JSON.stringify([{spec:{},price:0,num:0,status:'0',isDefault:'0'}])==JSON.stringify(newvalue));
            if (JSON.stringify([{spec: {}, price: 0, num: 0, status: '0', isDefault: '0'}]) == JSON.stringify(newvalue)) {
                app.entity.itemList=[];
            }
        },
        //监控变量的变化，如果是已经
        'entity.tbGoods.isEnableSpec':function (newvalue, oldvalue) {
            //如果是隐藏规格列表，则清除所有数据，展开是在进行选择
            if (newvalue == 0) {
                this.entity.tbGoods.specificationItems=[];
                this.entity.itemList=[];
            }
        },
        //entity.goods.category1Id 为要监听变量 ，当发生变化时 触发函数，newval 表示的是新值，oldvalue 表示的是旧值
        'entity.tbGoods.category1Id': function (newvalue, oldvalue) {
            //赋值为空
            app.itemCat3List=[];
            //删除属性回到原始状态
            if (this.entity.tbGoods.id == null) {
                delete this.entity.tbGoods.category2Id;

                delete this.entity.tbGoods.category3Id;

                delete this.entity.tbGoods.typeTemplateId;
            }
            if (newvalue != undefined) {
                axios.get("/itemCat/findByParentId/" + newvalue + ".shtml").then(function (response) {

                    app.itemCat2List = response.data;
                }).catch(function (error) {
                    console.log("1231312131321");
                })
            }
        },

        'entity.tbGoods.category2Id': function (newvalue, oldvalue) {
            //删除
            if (this.entity.tbGoods.id == null) {
                delete this.entity.tbGoods.category3Id;
                delete this.entity.tbGoods.typeTemplateId;
            }
            if (newvalue != undefined) {
                axios.get("/itemCat/findByParentId/" + newvalue + ".shtml").then(function (response) {
                    app.itemCat3List = response.data;
                }).catch(function (error) {
                    console.log("1231312131321");
                })
            }
        },

        'entity.tbGoods.category3Id': function (newvalue, oldvalue) {
            if (newvalue != undefined) {
                axios.get("/itemCat/findOne/" + newvalue + ".shtml").then(function (response) {
                    //获取列表数据 三级分类的列表
                    // app.entity.tbGoods.typeTemplateId = response.data.typeId;
                    //第一个参数：需要改变的值的对象变量
                    //第二个参数：需要赋值的属性名
                    //第三个参数：要赋予的值
                    app.$set(app.entity.tbGoods, 'typeTemplateId', response.data.typeId);
                    console.log(response.data.typeId);
                    console.log(app.entity.tbGoods.typeTemplateId)
                }).catch(function (error) {

                })
            }
        },

        'entity.tbGoods.typeTemplateId': function (newval, oldval) {
            if (newval != undefined) {

                axios.get('/typeTemplate/findOne/' + newval + '.shtml').then(
                    function (response) {

                        //获取到的是模板的对象
                        var typeTemplate = response.data;
                        //品牌的列表
                        app.brandTextList=JSON.parse(typeTemplate.brandIds);//[{"id":1,"text":"联想"}]

                        //获取模板中的扩展属性赋值给desc中的扩展属性属性值。
                        if (app.entity.tbGoods.id == null) {
                            app.entity.tbGoodsDesc.customAttributeItems = JSON.parse(typeTemplate.customAttributeItems);
                        }


                    }
                )

                //监听模板的变化 根据模板的ID 获取莫把的规格的数据拼接成要的格式
                axios.get("/typeTemplate/findSpecList/"+newval+".shtml").then(function (response) {
                    app.specList = response.data;
                })
            }
        }


    },
    //钩子函数 初始化了事件和
    created: function () {

        // this.searchList(1);
        this.findItemCat1List();

        //使用插件中的方法getUrlParam() 返回是一个JSON对象，例如：{id:149187842867989}
        var request = this.getUrlParam();
        //获取参数的值
        // console.log(request);
        //根据ID获取商品的信息
        this.findOne(request.id);
    }

})
