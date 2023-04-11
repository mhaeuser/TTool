# AI
AI intelligence in TTool intends to help designers to make better diagrams.

## Configuration
TTool must first be configured before using AI. Add the following information to the configuration file (e.g., config.xml) of TTool:
```xml
<OPENAIKey data="<put your secret key there>"/>
<OPENAIModel data="gpt-3.5-turbo"/>
```
This is required to have a valid openai account and available tokens.

## How to use AI?

First, select what you intend to do at the top of the AI window:

### Chat
This option makes it possible to directly interact with the AI.

Enter your question in the "Question" textarea, and click on "Start"

### Classify requirements
This options intends to automatically identify the "kind" attributes of requirements. Open a requirement diagram with at least one requirement, and click on "start". If the classification proposed by the AI (see the "Answer text area") suits you, you can click on "Apply response" to update the opened requirement diagram.


