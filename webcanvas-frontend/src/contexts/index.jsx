import { createContext, useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { EMPTY_MESSAGES } from "@/constants/message.js";
import { GitCommit, MessageCircle } from "lucide-react";
import { Slide, ToastContainer } from "react-toastify";

const ApplicationContext = createContext(null);

export function ApplicationContextProvider({ children }) {
  const navigate = useNavigate();
  /**
   * ========================= states ===========================
   */
  const init = {
    selectedTopTabIndex: -1,
    topTabItems: [],
    leftSidebarItems: [],
    leftSidebarEmptyPlaceholder: EMPTY_MESSAGES.GENERIC,
    leftSidebarTitle: {
      label: "main",
      icon: <GitCommit size={20} className="text-gray-400" />,
      button: false,
      onClick: () => {},
    },
    leftSidebarFooter: <></>,
    rightSidebarTitle: {
      label: "chat",
      icon: <MessageCircle size={20} className="text-gray-400" />,
      button: false,
      onClick: () => {},
    },
    rightSidebarFooter: <></>,
  };

  /**
   * 상단 바 관련 state
   */
  const [selectedTopTabIndex, setSelectedTopTabIndex] = useState(init.selectedTopTabIndex);
  const [topTabItems, setTopTabItems] = useState(init.topTabItems);

  /**
   * 좌측 sidebar 관련 state
   */
  const [leftSidebarItems, setLeftSidebarItems] = useState(init.leftSidebarItems);
  const [leftSidebarEmptyPlaceholder, setLeftSidebarEmptyPlaceholder] = useState(
    init.leftSidebarEmptyPlaceholder
  );
  const [leftSidebarTitle, setLeftSidebarTitle] = useState(init.leftSidebarTitle);
  const [leftSidebarFooter, setLeftSidebarFooter] = useState(init.leftSidebarFooter);

  /**
   * 우측 sidebar 관련 state
   */
  const [rightSidebarTitle, setRightSidebarTitle] = useState(init.rightSidebarTitle);
  const [rightSidebarFooter, setRightSidebarFooter] = useState(init.rightSidebarFooter);

  /**
   * ========================= states ===========================
   */

  /**
   * 상단 탭 관련 context
   * @type {{selectedIndex: number, onSelected: (function(*): void), items: *[], clear: topTabs.clear, setValue: topTabs.setValue}}
   */
  const topTabs = {
    selectedIndex: selectedTopTabIndex,
    onSelected: (index) => setSelectedTopTabIndex(index),
    items: topTabItems,
    clear: () => {
      setTopTabItems(init.topTabItems);
      setSelectedTopTabIndex(init.selectedTopTabIndex);
    },
    setItems: (value) => {
      setTopTabItems(value);
      setSelectedTopTabIndex(0);
    },
  };

  /**
   * 좌측 sidebar 관련 context
   * @type {{}}
   */
  const leftSidebar = {
    items: leftSidebarItems,
    emptyPlaceholder: leftSidebarEmptyPlaceholder,
    title: leftSidebarTitle,
    footer: leftSidebarFooter,
    setItems: (value) => {
      setLeftSidebarItems(value);
    },
    setEmptyPlaceholder: (value) => {
      setLeftSidebarEmptyPlaceholder(value);
    },
    setTitle: (value) => {
      setLeftSidebarTitle(value);
    },
    setFooter: (value) => {
      setLeftSidebarFooter(value);
    },
    clear: () => {
      setLeftSidebarItems(init.leftSidebarItems);
      setLeftSidebarEmptyPlaceholder(init.leftSidebarEmptyPlaceholder);
      setLeftSidebarTitle(init.leftSidebarTitle);
      setLeftSidebarFooter(init.leftSidebarFooter);
    },
  };

  /**
   * 우측 sidebar 관련 context
   * @type {{}}
   */
  const rightSidebar = {
    title: rightSidebarTitle,
    footer: rightSidebarFooter,
    clear: () => {
      setRightSidebarTitle(init.rightSidebarTitle);
      setRightSidebarFooter(init.rightSidebarFooter);
    },
  };

  useEffect(() => {
    console.log(leftSidebarItems);
  }, [leftSidebarItems]);

  return (
    <ApplicationContext.Provider
      value={{
        topTabs,
        leftSidebar,
        rightSidebar,
      }}
    >
      <ToastContainer
        position={"top-center"}
        limit={1}
        closeButton={false}
        autoClose={1500}
        hideProgressBar
        closeOnClick
        theme={"dark"}
        transition={Slide}
      />
      {children}
    </ApplicationContext.Provider>
  );
}

/**
 * 전역에서 Hook 사용
 * @returns {null}
 */
export function useApplicationContext() {
  const context = useContext(ApplicationContext);

  if (!context) {
    throw new Error("ApplicationContext Provider 안에서만 사용 가능합니다.");
  }

  return context;
}
