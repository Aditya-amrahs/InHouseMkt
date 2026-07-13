const EmptyState = ({ icon, message, children }) => {
  return (
    <div className="empty-state animate-fade-in">
      <div className="icon">{icon}</div>
      <p>{message}</p>
      {children && <div className="mt-4">{children}</div>}
    </div>
  );
};

export default EmptyState;
