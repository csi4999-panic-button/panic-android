"use strict";

const mongoose = require("mongoose");

const classroomSchema = new mongoose.Schema({
    schoolId: { type: "ObjectId", ref: "School" },
    courseType: { type: String },
    courseNumber: { type: String },
    sectionNumber: { type: String },
    courseTitle: { type: String, required: true },
    teachers: [{ type: "ObjectId", required: true, index: true, ref: "User" }],
    teacherAssistants: [{ type: "ObjectId",  default: [], index: true, ref: "User" }],
    students: [{ type: "ObjectId",  default: [], index: true, ref: "User" }],
    teacherCode: { type: "ObjectId", ref: "InviteCode" },
    taCode: { type: "ObjectId", ref: "InviteCode" },
    studentCode: { type: "ObjectId", ref: "InviteCode" }
}, {
    timestamps: true,
})



module.exports = mongoose.model("Classroom", classroomSchema);

