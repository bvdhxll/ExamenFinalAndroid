package com.programacionandroid.examenfinal

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import org.json.JSONObject

class MQTTClient {
    private lateinit var mqttClient: MqttClient


    fun initClient(serverURI: String, clientId: String) {
        mqttClient = MqttClient(serverURI, clientId, MqttDefaultFilePersistence())
        val options = MqttConnectOptions()
        options.isCleanSession = true
        mqttClient.connect(options)
    }

    fun publishMessage(topic: String, message: String) {
        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttMessage.qos = 2
            mqttClient.publish(topic, mqttMessage)
            println("Mensaje publicado en el tópico $topic: $message")
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun onMessageArrived(topic: String, message: String) {
        println("Mensaje recibido en el tópico $topic: $message")
        if (validateMessage(message)) {
            val jsonMessage = JSONObject(message)
            println("Campo 'data' del mensaje: ${jsonMessage.optString("data")}")
        } else {
            println("Mensaje inválido: $message")
        }
    }

    fun validateMessage(message: String): Boolean {
        return try {
            val jsonObject = JSONObject(message)
            jsonObject.has("data") && jsonObject.has("timestamp")
        } catch (e: Exception) {
            false
        }
    }

    fun subscribeToTopic(topic: String) {
        try {
            mqttClient.subscribe(topic) { t, msg ->
                onMessageArrived(t, String(msg.payload))
            }
            println("Suscrito al tópico: $topic")
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnectClient() {
        try {
            mqttClient.disconnect()
            println("Cliente MQTT desconectado")
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}