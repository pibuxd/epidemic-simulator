import React from "react";
import { createRoot } from "react-dom/client";
import EpidemicFrontend from "./EpidemicFrontend";
import "./styles.css";

const App = () => {
  return React.createElement(EpidemicFrontend, null);
};

createRoot(document.getElementById("root")).render(React.createElement(App, null));
