<template>
<div>
  <div><Nav></div>
  <div class="chat">
    <form action="#">
      <label for="name">Name: </label><input id="name" type="text" />
      <input id="join" type="button" value="Join!" />
    </form>

    <form action="#">
      <label for="message">Say something: </label
      ><input id="message" type="text" />
      <input id="send" type="button" value="Send" disabled="true" />
    </form>

    <div id="playground" />
  </div>
</div>
</template>

<script>
export default {
  name: "Chat",
  props: {
    msg: String
  },
  mounted: function() {
    var main = MainScalaJs
    main.start()
  }
};
</script>

<style scoped>
</style>