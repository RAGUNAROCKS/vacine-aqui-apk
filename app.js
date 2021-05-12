const express = require('express')
const app = express()
const mongoClient = require('mongodb').MongoClient

const url = 'mongodb+srv://Ragnus:@1234@vacineaqui.qh1x6.mongodb.net/BancoPosto?retryWrites=true&w=majority'

app.use(express.json())

mongoClient.connect(url, (err, db) => {

    if(err){
        console.log("Error while connecting mongo client")
    }else{
        const myDb = db.db('BancoPosto')
        const collection = myDb.collection('PostoDeVacina')

        app.post('/filometro', (req, res) => {

                    const newUser = {
                        id: req.body.id,
                        disponibilidade: req.body.disponibilidade,
                        pacientes: req.body.pacientes,
                        enfermeiros: req.body.enfermeiros
                    }

                    const query = {id: newUser.id}

                    const update = {$set:{disponibilidade: newUser.disponibilidade,
                                          pacientes: newUser.pacientes,
                                          enfermeiros: newUser.enfermeiros
                                    }}

                    collection.findOneAndUpdate(query, update, (err, result) => {
                        if (result != null){
                            res.status(200).send()
                        } else {
                            res.status(400).send()
                        }
                    })
                })

        app.post('/login', (req, res) =>{

            const query = {
                id: req.body.id,
                senha: req.body.senha
            }

            collection.findOne(query, (err, result) => {
                if(result != null) {
                   const objToSend = {
                       id: result.id,
                       senha: result.senha
                   }
                    res.status(200).send(JSON.stringify(objToSend))
                } else {
                    res.status(404).send()
                }
            })
        })

        app.post('/find', (req, res) =>{

                    const query = {
                        id: req.body.id
                    }

                    collection.findOne(query, (err, result) => {
                        if(result != null) {
                           const objToSend = {
                               nome: result.nome,
                               latitude: result.latitude,
                               longitude: result.longitude,
                               disponibilidade: result.disponibilidade,
                               pacientes: result.pacientes,
                               enfermeiros: result.enfermeiros
                           }
                            res.status(200).send(JSON.stringify(objToSend))
                        } else {
                            res.status(404).send()
                        }
                    })
                })

        app.get('/findAll', (req, res) =>{
                            collection.find().toArray(function (err, result){
                            if(result != null){
                                res.status(200).send(result)
                            }else{
                                res.status(404).send()
                            }
                        })
                        })
    }
})



app.listen(3000, () => {
    console.log("Listening on port 3000...")
})