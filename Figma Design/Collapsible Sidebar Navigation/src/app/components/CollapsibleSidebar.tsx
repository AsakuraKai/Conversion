import { useState } from 'react';
import {
  Home,
  FolderOpen,
  Youtube,
  Music,
  Camera,
  Chrome,
  Twitter,
  Image,
  Mail,
  Phone,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';

interface NavItem {
  id: string;
  label: string;
  icon: React.ElementType;
}

const navItems: NavItem[] = [
  { id: 'home', label: 'Home', icon: Home },
  { id: 'files', label: 'My Files', icon: FolderOpen },
  { id: 'youtube', label: 'YouTube', icon: Youtube },
  { id: 'spotify', label: 'Spotify', icon: Music },
  { id: 'camera', label: 'Camera', icon: Camera },
  { id: 'chrome', label: 'Chrome', icon: Chrome },
  { id: 'twitter', label: 'Twitter', icon: Twitter },
  { id: 'photos', label: 'Photos', icon: Image },
  { id: 'gmail', label: 'Gmail', icon: Mail },
  { id: 'phone', label: 'Phone', icon: Phone },
];

export function CollapsibleSidebar() {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [activeItem, setActiveItem] = useState('home');

  const toggleSidebar = () => {
    setIsCollapsed(!isCollapsed);
  };

  const handleNavItemClick = (itemId: string) => {
    setActiveItem(itemId);
    // Auto-collapse sidebar when switching to a different section
    if (itemId !== activeItem) {
      setIsCollapsed(true);
    }
  };

  const handleMainContentClick = () => {
    // Collapse sidebar when clicking main content area
    if (!isCollapsed) {
      setIsCollapsed(true);
    }
  };

  return (
    <div className="flex h-screen bg-black">
      {/* Sidebar */}
      <div
        className={`bg-black text-white transition-all duration-300 ease-in-out ${
          isCollapsed ? 'w-16' : 'w-56'
        } flex flex-col border-r border-zinc-800`}
      >
        {/* Header with Date */}
        <div className="p-6 flex flex-col items-start">
          {!isCollapsed && (
            <>
              <div className="text-4xl font-light mb-1">29</div>
              <div className="text-xs text-zinc-500">January 2026</div>
            </>
          )}
          {isCollapsed && (
            <div className="text-2xl font-light">29</div>
          )}
        </div>

        {/* Navigation Items */}
        <nav className="flex-1 py-2 overflow-y-auto">
          <ul className="space-y-0.5">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = activeItem === item.id;

              return (
                <li key={item.id}>
                  <button
                    onClick={() => handleNavItemClick(item.id)}
                    className={`w-full flex items-center gap-4 px-6 py-3.5 transition-all ${
                      isActive
                        ? 'bg-zinc-800 text-white'
                        : 'text-zinc-400 hover:bg-zinc-900 hover:text-white'
                    } ${isCollapsed ? 'justify-center px-0' : ''}`}
                    title={isCollapsed ? item.label : undefined}
                  >
                    <Icon className="w-5 h-5 flex-shrink-0" />
                    {!isCollapsed && (
                      <span className="text-sm whitespace-nowrap">{item.label}</span>
                    )}
                  </button>
                </li>
              );
            })}
          </ul>
        </nav>

        {/* Toggle Button */}
        <div className="p-4 border-t border-zinc-800">
          <button
            onClick={toggleSidebar}
            className="w-full flex items-center justify-center gap-2 p-2.5 hover:bg-zinc-900 rounded-lg transition-colors text-zinc-400 hover:text-white"
            aria-label={isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          >
            {isCollapsed ? (
              <ChevronRight className="w-5 h-5" />
            ) : (
              <>
                <ChevronLeft className="w-5 h-5" />
                <span className="text-sm">Collapse</span>
              </>
            )}
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 bg-zinc-950 overflow-auto" onClick={handleMainContentClick}>
        <div className="p-8">
          <div className="max-w-5xl mx-auto">
            <h1 className="text-4xl text-white mb-8">
              {navItems.find((item) => item.id === activeItem)?.label}
            </h1>
            
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[1, 2, 3, 4, 5, 6].map((i) => (
                <div
                  key={i}
                  className="bg-zinc-900 rounded-xl p-6 border border-zinc-800 hover:border-zinc-700 transition-colors"
                >
                  <div className="w-12 h-12 bg-zinc-800 rounded-lg mb-4 flex items-center justify-center">
                    <div className="w-6 h-6 bg-zinc-700 rounded"></div>
                  </div>
                  <h3 className="text-white font-medium mb-2">Item {i}</h3>
                  <p className="text-sm text-zinc-500">
                    Content preview for this item goes here
                  </p>
                </div>
              ))}
            </div>

            <div className="mt-8 bg-zinc-900 rounded-xl p-6 border border-zinc-800">
              <h2 className="text-xl text-white mb-4">About This View</h2>
              <p className="text-zinc-400 leading-relaxed">
                This is the main content area that automatically expands when
                the sidebar collapses. Click the navigation items to switch
                between different sections, and use the collapse button at the
                bottom of the sidebar to toggle between icon-only and
                full-width modes.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}