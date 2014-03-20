package models


import org.bson.types.ObjectId

/**
 * Embed Structure.
*/
case class OnUsePicture(
    fileObjId: ObjectId = new ObjectId,
    picUse: String,                           // Ref to the PictureUse Master Table, but we only use the [picUseName] field as a key.
    showPriority: Option[Int],
    description: Option[String]
)


/**
 * Embed Structure.
*/
case class OptContactMethod (
    contMethmodType: String,
    account: List[String]
)
