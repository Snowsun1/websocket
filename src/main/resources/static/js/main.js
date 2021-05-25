'use strict';
let usernameForm = document.querySelector('#usernameForm');
let messageForm = document.querySelector('#messageForm');
let messageInput = document.querySelector('#message');
let new_message_input = document.querySelector('#new_message');
let new_message_form = document.querySelector('#new_message_form');
let stompClient = null;
let username = null;
let to_user = "default";
let chat_list = new Set()
let isChatloaded = false;
let currMessages = []

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function getNewMessages(){
  while (true){
    if(to_user==null || to_user==="default") {
      await  sleep(500)
      continue
    }
    stompClient.send("/app/chat.getChatMessages",{},
        JSON.stringify({
          "firstPerson": username,
          "secondPerson": to_user
        }))
    await sleep(1000)
  }

}
function socket_connect(username_var) {
  document.querySelector('#userLogin').style.display = "none";
  document.querySelector("#messanger").style.display = "flex";
  const socket = new SockJS('/ws');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, onConnected);
  username = username_var;
  localStorage.setItem("username", JSON.stringify({
    "username": username_var
  }));
}

function light() {
  let name = document.querySelector('#name');
  name.setAttribute("style", "border: 3px solid #ff5652 !important");
  setTimeout(() => {
    name.setAttribute("style", "");
  }, 100);
  setTimeout(() => {
    name.setAttribute("style", "border: 3px solid #ff5652 !important");
  }, 200);
  setTimeout(() => {
    name.setAttribute("style", "");
  }, 300);
  setTimeout(() => {
    name.setAttribute("style", "border: 3px solid #ff5652 !important");
  }, 400);
  setTimeout(() => {
    name.setAttribute("style", "");
  }, 500);
}

function connect(event) {
  let name = document.querySelector('#name');
  username = name.value.trim();
  if (username && username !== "default") {
    socket_connect(username);
  } else {
    light();
  }
  event.preventDefault();
}

function onConnected() {
  // Subscribe to the Public Topic
  // stompClient.subscribe('/topic/public', onMessageReceived);
  // Tell your username to the server

  stompClient.send("/app/chat.addUser",
      {},
      JSON.stringify({login: username, type: 'JOIN'})
  )
  console.log("subscribing to newUserEvent...")
  stompClient.subscribe('/topic/newUserEvent', newUserEvent)
  console.log("subscribing to loadChats...")
  stompClient.subscribe("/topic/chat.getUserChats", loadChats)
  stompClient.send("/app/chat.getChats", {}, JSON.stringify({
    "login": username
  }));
  stompClient.subscribe("/topic/chat.getNewChatMessage", handleMessage)
  getNewMessages()
}

function handleMessage(event){

}
let prev = null
function loadChats(event) {
  if(isChatloaded) return;
  isChatloaded = true
  let chats = JSON.parse(event.body)
  for(let i =0;i<chats.length;i++){
    console.log(chats[i])
    console.log(username)
    if(chats[i].username===username) return;
  }
  let all_chats = document.getElementById("all_chats");
  const parent_all = all_chats.parentNode;
  all_chats.parentNode.removeChild(all_chats);
  all_chats = document.createElement("div");
  all_chats.setAttribute("id", "all_chats");
  all_chats.setAttribute("class", "messages-box");
  parent_all.appendChild(all_chats);
  chats.forEach(chat => addToChat(chat))
}

function newUserEvent(event) {
  let chat = event.body
  if (chat === "" || chat === username) {
    return;
  }
  chat = {
    user: chat,
    date: "",
    message: "",
    is_online: true
  }
  addToChat(chat)
}

function addToChat(chat) {
  let all_chats = document.getElementById("all_chats");
  $("#all_chats").append(
      allChats_without_current(chat)
  )
  all_chats.scrollTop = 0;
}

function sendMessage(event) {
  const messageContent = messageInput.value.trim();
  if (messageContent && stompClient) {
    const chatMessage = {
      sender: username,
      message: messageInput.value,
      date: new Date(),
      recipient: to_user,
      type: 'CHAT'
    };
    currMessages.push(chatMessage)
    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
    messageInput.value = '';
    let all_message = document.getElementById("all_message");
    all_message.scrollTop = 0;
  }
  event.preventDefault();
}
let loaded =false;
function change_chat(user) {
  to_user = user;
  stompClient.subscribe("/topic/chat.getChatMessages", function (message) {
    if (message.body === undefined) {
      return false;
    }
    let messages = JSON.parse(message.body);
    let all_message = document.getElementById("all_message");
    for (let i = 0; i < messages.length; i++) {
      let flat = false;
      for(let j = 0;j<currMessages.length;j++){
        if(currMessages[j].date===messages[i].date){
          flat=true
        }
      }
      if(flat) continue
      currMessages.push(messages[i])
      if (messages[i]["sender"] === username) {
        $("#all_message").append(
            ourMessage(messages[i])
        )
      } else {
        $("#all_message").append(
            foreignMessage(messages[i])
        )
      }
    }
    all_message.scrollTop = 0;
  });
  return false;
}

function foreignMessage(message) {
  let date = message["time"];
  const text = message["message"];
  const fromUser = message["sender"];
  if (date == null) {
    date = ""
  } else {
    date = new Date(date)
    date = date.getHours() + ":" + date.getMinutes();
  }
  return ` <div class="media w-50 mb-3">
          <div class="media-body ml-3">
            <div class="bg-light rounded py-2 px-3 mb-2">
              <p class="text-small mb-0 text-muted">${text}</p>
              <br>
              <p class="small text-muted">From: <strong color="black">${fromUser}</strong>, ${date} </p>
            </div>
          </div>
        </div> `;
}

function ourMessage(message) {
  let date = message["date"];
  const text = message["message"];
  if (date == null) {
    date = ""
  } else {
    date = new Date(date)
    date = date.getHours() + ":" + date.getMinutes();
  }
  return `<div class="media w-50 ml-auto mb-3">
          <div class="media-body">
            <div class="bg-primary rounded py-2 px-3 mb-2">
              <p class="text-small mb-0 text-white">${text}</p>
            </div>
            <p class="small text-muted">${date}</p>
          </div>
        </div>`
}

function allChats_without_current(chat) {
  const is_online = chat["is_online"] ? "online-indicator"
      : "offline-indicator";
  const text = chat["message"];
  const user = chat["user"];
  let date = chat["date"];
  if (date == null) {
    date = ""
  } else {
    date = new Date()
    date = date.getHours() + ":" + date.getMinutes();
  }
  return `<a class="list-group-item list-group-item-action list-group-item-light rounded-0"
             onclick=change_chat(\'${user}\')>
              <div class="media">
             
                <div class="media-body ml-4">
                  <div class="d-flex align-items-center justify-content-between mb-1">
                    <h6 class="mb-0">${user}<div class="${is_online}"></div></h6>
                  </div>
                                      
                </div>
              </div>
            </a>`
}

function new_message(event) {
  // let new_message_send = document.querySelector('#new_message_send');
  const new_message_input_value = new_message_input.value.trim();
  if (new_message_input_value && stompClient) {
    const chatMessage = {
      sender: username,
      message: "Автомессендж, это костыль",
      date: new Date(),
      recipient: new_message_input_value,
      type: 'CHAT'
    };
    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
  }
  event.preventDefault();
}

let username_local = localStorage.getItem("username");

if (username_local !== null) {
  let username_obj = JSON.parse(username_local);
  if (username_obj["username"] !== null) {
    socket_connect(JSON.parse(username_local)["username"]);
  }
}

usernameForm.addEventListener('submit', connect, false);
messageForm.addEventListener('submit', sendMessage, false);
new_message_form.addEventListener('submit', new_message, false);
