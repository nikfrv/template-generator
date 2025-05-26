import React from "react";

type Props = {
  userEmail: string;
  profileMenu: boolean;
  setProfileMenu: (v: boolean) => void;
  onLogout: () => void;
};

const ProfileMenu: React.FC<Props> = ({ userEmail, profileMenu, setProfileMenu, onLogout }) => (
  <div className="relative">
    <button
      className="px-6 py-2 border border-blue-700 rounded-2xl font-semibold text-blue-700 bg-white hover:bg-blue-50 transition shadow-sm"
      onClick={() => setProfileMenu(!profileMenu)}
    >
      {userEmail}
    </button>
    {profileMenu && (
      <div className="absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-2xl shadow z-50">
        <button
          className="block w-full text-left px-4 py-2 hover:bg-blue-50 rounded-2xl"
          onClick={onLogout}
        >
          Выйти
        </button>
      </div>
    )}
  </div>
);

export default ProfileMenu;