
**General Approach**

 - Spent a couple of hours a day on 3 separate days around other   
   responsibilities.
	- I started small and iterated using these steps:
   	Started with a skeleton implementation of the view with solo
   messages. 	
   - Tidied up the view to try and match the designs.
   - Added logic and view changes for gapping and timestamps. 
   - Added the logic for auto-replies. 	
   - Included some Jetpack Compose UI tests. This may have been unnecessary, but I wanted to show that I could do this.
   

**Caveats**

 - I am by no means an expert with Jetpack Compose, but we have been
   using it at JustPark, and I am aware you use it at Muzz, so I wanted
   to show I was at least capable. 	
 - I tried to match the design pretty closely, except for the header, which is basic and pants. 	
 - I have over-commented on the files. I don’t think in a real app this amount
   of commenting is warranted, but I wanted to convey my understanding
   of what I was doing.
   

**Implementation Notes**

 - I hadn’t created a new app with Toml before, but I found it super
   useful. I discovered that I could just try to import libraries into
   the class I was using, and the dependencies would be added
   automatically.
   
 - The folder structure (di, data, util, ui) is designed to separate
   concerns, making the codebase modular, scalable, and easier to
   maintain by organizing dependency injection, data management, utility
   functions, and user interface components (including VMs) into clear
   layers.
   
 - I chose the MVVM architecture with repositories to decouple the UI
   from the business logic, ensuring that the ViewModel handles state
   and logic while the repository abstracts data sources. This approach
   improves testability, reusability, and allows us to plug and play
   different data sources (e.g., local database, network).

 - I created a Preview for the whole ChatScreen, passing in a ViewModel
   (which actually is not recommended for larger projects). This helped
   me instantly see changes to any of my composables and develop faster.

 - I split composables into their own files for readability:
   MessageBubble, ChatInput, ChatScreen, and AppTopBar.
   
 - Used Dagger/Hilt for dependency injection to allow testability, as I am used to it, but I am aware there are other options for DI such as Koin etc.
   
 - Reversed messages so that when opening the soft keyboard, the last
   currently visible message is aligned with the top of the keyboard.
   
 - I kept the logic in the ViewModel and created a basic response
   mechanism where the bot replies to your latest message by sending you
   the same thing back scrambled. It waits for 5 seconds to respond and
   does not respond unless those 5 seconds pass with no other message
   being received.
   

 - Used Room for the database, it was a pretty simple implementation but I ensured the viewmodel reacted to the database, rather than updating the view from the sendMessage directly.
 

**Assumptions**

 - I wasn’t sure if “{day} {timestamp}” was correct, as in whether we
   want to show the Unix timestamp for the message, so I went with the
   same format as the screenshot.
   
 - The text in the TextField in the screenshot is aligned slightly
   higher. With Compose Material3, I ended up using a BasicTextField for
   more control rather than the easier OutlinedTextField.
   
 - Assumed that the TextField and button were only “active” if there was
   text written inside, as the TextField is not focused in the first
   screenshot but has text in it and is in an active state.
   

**What I Would Do With More Time**

 - Dimensions could probably be separated and reused based on tokens in
   Figma or whatever platform you use for design.
   
 - Allow the TextField to expand when typing longer messages, up to 5/6
   lines. Currently, it goes out of the top of the box if you type too
   many characters.
   
 - Make the AppBar look a little less horrible.
 
 - Add a loading state to indicate the other user is typing. Write some
   more tests, covering the data layer.
   

**Further Suggestions for Improvement**

 - Add more chats.
 
 - Hook up with a backend using WebSockets and possibly API calls for
   sending messages.
  
 - Paginate message calls.
