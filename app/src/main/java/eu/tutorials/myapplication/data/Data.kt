package eu.tutorials.myapplication.data

data class UserData(
    val name:String?="",
    val number:String?="",
    val userId:String?="",
    val imageUrl:String?=""
)
{
    fun toMap()= mapOf(
        "userId" to userId,
        "name" to name,
        "number" to number,
        "imageUrl" to imageUrl
    )
}

data class ChatData(
    val chatId:String?="",
    val user1:ChatUser=ChatUser(),
    val user2:ChatUser=ChatUser()
)

data class ChatUser(
    val userId:String?="",
    val name:String?="",
    val number:String?="",
    val imageUrl:String?=""
)


data class  Message(
    val sentBy:String?="",
    val message:String?="",
    val timestamp:String?="",
    val imageUrl: String?=""

)
data class Status(
    val user: ChatUser=ChatUser(),
    val imageUrl: String?="",
    val timestamp: Long?=null
)
